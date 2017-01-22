package com.me.toptenCompare;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class ttcReducer extends Reducer<Text, IntWritable,Text, IntWritable> {
	
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		for (IntWritable value : values) {
			context.write(key, value);
		}
		
	}

}
