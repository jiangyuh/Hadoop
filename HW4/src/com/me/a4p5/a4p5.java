package com.me.a4p5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.me.a4p5.MedianStDevTuple;


public class a4p5 {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = new Job(conf,"MedianStdDev");
		job.setJarByClass(a4p5.class);
		job.setMapperClass(devMapper.class);
		job.setCombinerClass(SOMedianStdDevCombiner.class);
		job.setReducerClass(SOMedianStdDevReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(SortedMapWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MedianStDevTuple.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
		// TODO Auto-generated method stub
		
		public static class devMapper extends Mapper<Object, Text, Text, SortedMapWritable> {
			private static final DoubleWritable one= new  DoubleWritable(1.0);
			private DoubleWritable volumn=new DoubleWritable(0.0);
			public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
				String s = value.toString();
				StringTokenizer itr = new StringTokenizer(s);
				while (itr.hasMoreTokens()) {
					String s2 = itr.nextToken();
					String a[] = s2.split("::");
						String key1 = a[1];
						double value1 = Double.parseDouble(a[2]);
						SortedMapWritable outLength= new SortedMapWritable();
						volumn.set(value1);
						outLength.put(volumn, one);
						context.write(new Text(key1),outLength);
				}
			}
		}
		public static class SOMedianStdDevCombiner extends Reducer<Text, SortedMapWritable, Text, SortedMapWritable>
		{
	       protected void reduce(Text key,Iterable<SortedMapWritable> values, Context context)
	    		   throws IOException, InterruptedException {
		SortedMapWritable outValue = new SortedMapWritable();
		for (SortedMapWritable v : values) {
			for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
				DoubleWritable count = (DoubleWritable) outValue.get(entry.getKey());

				if (count != null) {
					count.set(count.get()+ ((DoubleWritable) entry.getValue()).get());
				} else {
					outValue.put(entry.getKey(), new DoubleWritable(((DoubleWritable) entry.getValue()).get()));
				}
			}
			v.clear();
		}

		context.write(key, outValue);
	}
}

		public static class SOMedianStdDevReducer extends Reducer<Text, SortedMapWritable, Text, MedianStDevTuple>
		{
	private MedianStDevTuple result = new MedianStDevTuple();
	private TreeMap<Double, Double> commentLengthCounts = new TreeMap<Double, Double>();

	public void reduce(Text key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException {

		double sum = 0;
		double totalComments = 0;
		commentLengthCounts.clear();
		result.setMedian(0);
		result.setStdDev(0);

		for (SortedMapWritable v : values) {
			for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
				double length = ((DoubleWritable) entry.getKey()).get();
				double count = ((DoubleWritable) entry.getValue()).get();

				totalComments += count;
				sum += length * count;

				Double storedCount = commentLengthCounts.get(length);
				if (storedCount == null) {
					commentLengthCounts.put(length, count);
				} else {
					commentLengthCounts.put(length, storedCount + count);
				}
			}
		}

		Double medianIndex = totalComments / 2;
		double previousComments = 0;
		double comments = 0;
		double prevKey = 0;
		for (Entry<Double, Double> entry : commentLengthCounts.entrySet()) {
			comments = previousComments + entry.getValue();
			if (previousComments <= medianIndex && medianIndex < comments) {
				if (totalComments % 2 == 0) {
					if (previousComments == medianIndex) {
						result.setMedian((double) (entry.getKey() + prevKey) / 2.0f);
					} else {
						result.setMedian(entry.getKey());
					}
				} else {
					result.setMedian(entry.getKey());
				}
				break;
			}
			previousComments = comments;
			prevKey = entry.getKey();
		}

		// calculate standard deviation
		double mean = sum / totalComments;

		float sumOfSquares = 0.0f;
		for (Entry<Double,Double> entry : commentLengthCounts.entrySet()) {
			sumOfSquares += (entry.getKey() - mean)
					* (entry.getKey() - mean) * entry.getValue();
		}

		result.setStdDev((double) Math.sqrt(sumOfSquares
				/ (totalComments - 1)));

		context.write(key, result);
	}
}

		
		
			
		}

