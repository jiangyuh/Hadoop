package com.me.a4p2;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class MinMaxMapper extends Mapper<Object, Text, Text, MinMaxTuple> {
	private MinMaxTuple mmt = new MinMaxTuple();
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

	   String s = value.toString();
       StringTokenizer itr = new StringTokenizer(s);
       while (itr.hasMoreTokens()) {
           String s2 = itr.nextToken();
           String a[] = s2.split(",");
           if(a.length>2){
           if (!a[7].equals("stock_volume") && a.length > 4) {
        	   String key1 = a[1];
				String stockDate = a[2];
				String stockAdjClose = a[8];
				String stockVolume = a[7];
				mmt.setMaxDate("0");
				mmt.setMinDate("2");
				mmt.setStockAdjClose(stockAdjClose);
				mmt.setStockDate(stockDate);
				mmt.setStockVolume(stockVolume);
				context.write(new Text(key1), mmt);
           }
           }
           }
 } 
}


