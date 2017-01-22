package com.me.secondarysort;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class sortReducer extends Reducer<CompositeKeyWriteable, NullWritable, CompositeKeyWriteable, NullWritable>{
	
	public void reduce(CompositeKeyWriteable key, Iterable<NullWritable> values,
			Context context) throws IOException, InterruptedException {
		
		for (NullWritable value : values) {

			context.write(key, NullWritable.get());
		}
		
	}

}
