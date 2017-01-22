package com.me.secondarysort;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class sortPartitioner extends Partitioner<CompositeKeyWriteable,NullWritable> {

	@Override
	public int getPartition(CompositeKeyWriteable key, NullWritable value, int numPartitioner) {
		// TODO Auto-generated method stub
		return (key.getStars().hashCode()%numPartitioner);
	}

}
