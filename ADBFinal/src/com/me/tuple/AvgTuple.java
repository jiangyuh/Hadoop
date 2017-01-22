package com.me.tuple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class AvgTuple implements Writable{
	
	private String avgStar;
	private String starCount;
	
	

	public String getAvgStar() {
		return avgStar;
	}

	public void setAvgStar(String avgStar) {
		this.avgStar = avgStar;
	}

	public String getStarCount() {
		return starCount;
	}

	public void setStarCount(String starCount) {
		this.starCount = starCount;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		avgStar=WritableUtils.readString(arg0);
		starCount=WritableUtils.readString(arg0);
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		WritableUtils.writeString(arg0, avgStar);
		WritableUtils.writeString(arg0, starCount);
		
	}

	@Override
	public String toString() {
		return "-"+avgStar  + "-" +starCount;
	}

	
	
}
