package com.me.a4p4;

/*Using the MoviLens dataset, determine the median and standard deviation of comments per movie.
Iterate through the given set of values and add each value to
an in-memory list. The iteration also calculates a running sum and count.
 * */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class a4p4 {
	
	
	public static class devMapper extends Mapper<Object, Text, Text, DoubleWritable> {
		public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
			String s = value.toString();
			StringTokenizer itr = new StringTokenizer(s);
			while (itr.hasMoreTokens()) {
				String s2 = itr.nextToken();
				String a[] = s2.split("::");
					String key1 = a[1];
					double value1 = Double.parseDouble(a[2]);
					context.write(new Text(key1), new DoubleWritable(value1));
			}
		}
	}
	
	public static class AvgReducer extends Reducer<Text, DoubleWritable, Text, MedianStDevTuple> {

		private MedianStDevTuple result = new MedianStDevTuple();
		private ArrayList<Double> commentLengths = new ArrayList<Double>();
		
		public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
				throws IOException, InterruptedException {
			float sum = 0;
			float count = 0;
			commentLengths.clear();
			result.setStdDev(0);
			result.setMedian(0);
			
			// Iterate through all input values for this key
			for (DoubleWritable val : values) {
				commentLengths.add((double) val.get());
				sum += val.get();
				++count;
			}

			// sort commentLengths to calculate median
			Collections.sort(commentLengths);

			// if commentLengths is an even value, average middle two elements
			if (count % 2 == 0) {
				result.setMedian((commentLengths.get((int) count / 2 - 1) + commentLengths
						.get((int) count / 2)) / 2.0f);
			} else {
				// else, set median to middle value
				result.setMedian(commentLengths.get((int) count / 2));
			}

			// calculate standard deviation
			float mean = sum / count;

			float sumOfSquares = 0.0f;
			for (Double f : commentLengths) {
				sumOfSquares += (f - mean) * (f - mean);
			}

			result.setStdDev((double) Math.sqrt(sumOfSquares / (count - 1)));

			context.write(key, result);
			
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = new Job(conf,"MedianStdDev");
		job.setJarByClass(a4p4.class);
		job.setMapperClass(devMapper.class);
		job.setReducerClass(AvgReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MedianStDevTuple.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	

}
