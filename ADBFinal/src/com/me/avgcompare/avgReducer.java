package com.me.avgcompare;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;


public class avgReducer extends Reducer<Text, SortedMapWritable, Text, DoubleWritable>{
	private TreeMap<Double, Double> commentLengthCounts = new TreeMap<Double, Double>();

	public void reduce(Text key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException {

		double sum = 0;
		double totalComments = 0;
		commentLengthCounts.clear();

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


		// calculate standard deviation
		double mean = sum / totalComments;

		context.write(key, new DoubleWritable(mean));
	}

}
