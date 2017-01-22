package com.me.a4p2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MinMaxCombiner extends Reducer<Text,MinMaxTuple, Text,MinMaxTuple> {
	
	 public void reduce(Text key, Iterable<MinMaxTuple> values, Context context) throws IOException, InterruptedException {
		    MinMaxTuple minmax = new  MinMaxTuple();
		    MinMaxTuple close = new MinMaxTuple();
		    minmax.setMaxDate(null);
		    minmax.setMinDate(null);
		    minmax.setStockAdjClose("1");
			// result.setStockAdjClose(0);
			int maxStockVolume = 0;
			int minStockVolume = 0;
			for (MinMaxTuple val : values) {
				if (val.getStockVolume() != null && val.getStockAdjClose() != null) {
					if (maxStockVolume == 0 || maxStockVolume < Integer.parseInt(val.getStockVolume())) {
						maxStockVolume = Integer.parseInt(val.getStockVolume());
						minmax.setStockDate(val.getStockDate());
						minmax.setStockVolume(String.valueOf(maxStockVolume));
					}
					if (minStockVolume == 0 || minStockVolume > Integer.parseInt(val.getStockVolume())) {
						minStockVolume = Integer.parseInt(val.getStockVolume());
						close.setStockDate(val.getStockDate());
						close.setStockVolume(String.valueOf(minStockVolume));
					}

					if (minmax.getStockAdjClose() != null) {
						if (Double.parseDouble(minmax.getStockAdjClose()) == 0 || Double
								.parseDouble(minmax.getStockAdjClose()) < Double.parseDouble(val.getStockAdjClose())) {
							minmax.setStockAdjClose(val.getStockAdjClose());
							close.setStockAdjClose(val.getStockAdjClose());
						}

					}
				}
			}

			context.write(key, minmax);
			context.write(key, close);
 }
}


