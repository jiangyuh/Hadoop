package com.me.avgcompare;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class avgCombiner extends Reducer<Text, SortedMapWritable, Text, SortedMapWritable>{
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
