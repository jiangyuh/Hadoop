package com.me.secondarysort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class CompositeKeyWriteable implements Writable, WritableComparable<CompositeKeyWriteable>{

	public int review_count;
	public String stars;
	public String name;
	
	public  CompositeKeyWriteable()
	{}
	
	public int getReview_count() {
		return review_count;
	}

	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		 stars = WritableUtils.readString(arg0);
		 name = WritableUtils.readString(arg0);
		 review_count = arg0.readInt();
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		WritableUtils.writeString(arg0, stars);
		WritableUtils.writeString(arg0, name);
		arg0.writeInt(review_count);
	}

	@Override
	public int compareTo(CompositeKeyWriteable o) {
		// TODO Auto-generated method stub
		int result = -1*stars.compareTo(o.stars);
		if (0 == result) {
			result= -1*Integer.compare(review_count, o.review_count);
			//result = -1*name.compareTo(o.name);
		}
		return result;

	}
	@Override
	public String toString() {
		return stars+"--"+review_count+"--"+name;
	}
	
}
