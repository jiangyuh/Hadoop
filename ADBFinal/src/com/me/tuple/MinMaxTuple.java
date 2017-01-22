package com.me.tuple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class MinMaxTuple implements Writable {
	
	private String maxPrice;
	private String minPrice;
	
	
	
	public String getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}
	public String getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		maxPrice=WritableUtils.readString(arg0);
		minPrice=WritableUtils.readString(arg0);
		
	}
	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		WritableUtils.writeString(arg0, maxPrice);
		WritableUtils.writeString(arg0, minPrice);
		
	}
	@Override
	public String toString() {
		return "-"+maxPrice + "-" + minPrice;
	}
	
	

}
