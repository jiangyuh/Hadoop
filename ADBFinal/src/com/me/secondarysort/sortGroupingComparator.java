package com.me.secondarysort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;



public class sortGroupingComparator extends WritableComparator{
	
	protected sortGroupingComparator()
	{
		super(CompositeKeyWriteable.class, true);
	}
	
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		CompositeKeyWriteable key1 = (CompositeKeyWriteable) w1;
		CompositeKeyWriteable key2 = (CompositeKeyWriteable) w2;
		return -1*key1.getStars().compareTo(key2.getStars());
	}

}
