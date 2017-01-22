package com.me.a4p5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;


public class MedianStDevTuple implements Writable{
	
	private double median = 0;
	private double stddev = 0;

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public double getStdDev() {
		return stddev;
	}

	public void setStdDev(double stddev) {
		this.stddev = stddev;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		median = in.readFloat();
		stddev = in.readFloat();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(median);
		out.writeDouble(stddev);
	}

	@Override
	public String toString() {
		return median + "\t" + stddev;
	}

}
