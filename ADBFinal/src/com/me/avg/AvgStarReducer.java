package com.me.avg;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.me.tuple.AvgTuple;

public class AvgStarReducer extends Reducer<Text, DoubleWritable, Text, AvgTuple >{
	
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		AvgTuple result = new AvgTuple();
		result.setAvgStar(null);
		result.setStarCount(null);
		double sum = 0;
		int count = 0;
		for (DoubleWritable val : values) {
			sum += val.get();
			count++;
		}
		result.setAvgStar(String.valueOf(Math.round(sum / count * 100) / 100.0));
		result.setStarCount(String.valueOf(count));
		context.write(key, result);
		
	}

}
