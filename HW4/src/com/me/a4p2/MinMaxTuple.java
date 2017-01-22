package com.me.a4p2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class MinMaxTuple implements Writable {
	
	private String maxDate;
	private String minDate;
	private String stockDate;
	private String stockVolume;
	private String stockAdjClose;
	



	public String getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public String getMinDate() {
		return minDate;
	}

	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	public String getStockDate() {
		return stockDate;
	}

	public void setStockDate(String stockDate) {
		this.stockDate = stockDate;
	}

	public String getStockVolume() {
		return stockVolume;
	}

	public void setStockVolume(String stockVolume) {
		this.stockVolume = stockVolume;
	}

	public String getStockAdjClose() {
		return stockAdjClose;
	}

	public void setStockAdjClose(String stockAdjClose) {
		this.stockAdjClose = stockAdjClose;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		maxDate=WritableUtils.readString(arg0);
		minDate=WritableUtils.readString(arg0);
		stockDate=WritableUtils.readString(arg0);
		stockAdjClose=WritableUtils.readString(arg0);
		stockVolume=WritableUtils.readString(arg0);
		
	}


	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		WritableUtils.writeString(arg0, maxDate);
		WritableUtils.writeString(arg0, minDate);
		WritableUtils.writeString(arg0, stockDate);
		WritableUtils.writeString(arg0, stockAdjClose);
		WritableUtils.writeString(arg0, stockVolume);

	}

	@Override
	public String toString() {
		return stockDate + "\t" + stockAdjClose + "\t" + stockVolume+ "\t" + maxDate+ "\t" + minDate;
	}
	
	

}
