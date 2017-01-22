package com.me.toptenCompare;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SortReducer extends Reducer<IntWritable, Text,IntWritable,Text >{
	
	private static int counter = 0;
	public void reduce(IntWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		
		for (Text value : values) {
			counter++;
			if(counter<11)
			{
				context.write(key,value);
			}
		}
		
	}

}
