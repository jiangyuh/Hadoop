package com.me.a4p2;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MinMaxReducer extends Reducer<Text,MinMaxTuple, Text,MinMaxTuple>  {


    public void reduce(Text key, Iterable<MinMaxTuple> values,Context context) 
    		throws IOException, InterruptedException {

    	MinMaxTuple result = new MinMaxTuple();
		result.setMaxDate(null);
		result.setMinDate(null);
		result.setStockAdjClose("1");
		// result.setStockAdjClose(0);
		int maxStockVolume = 0;
		int minStockVolume = 0;
		for (MinMaxTuple val : values) {
			if (val.getStockVolume() != null && val.getStockAdjClose() != null) {
				if (maxStockVolume == 0 || maxStockVolume < Integer.parseInt(val.getStockVolume())) {
					maxStockVolume = Integer.parseInt(val.getStockVolume());
					result.setMaxDate(val.getStockDate());
					result.setStockVolume(String.valueOf(maxStockVolume));
				}
				if (minStockVolume == 0 || minStockVolume > Integer.parseInt(val.getStockVolume())) {
					minStockVolume = Integer.parseInt(val.getStockVolume());
					result.setMinDate(val.getStockDate());
					result.setStockVolume(String.valueOf(minStockVolume));
				}

				if (result.getStockAdjClose() != null) {
					if (Double.parseDouble(result.getStockAdjClose()) == 0 || Double
							.parseDouble(result.getStockAdjClose()) < Double.parseDouble(val.getStockAdjClose())) {
						result.setStockAdjClose(val.getStockAdjClose());
					}

				}
			}
		}
		result.setStockDate("");
		result.setStockVolume(""); 
		result.setStockAdjClose("Max stock_price_adj_close=" + result.getStockAdjClose());
		result.setMaxDate("MaxDate_Volume=" + result.getMaxDate());
		result.setMinDate("MinDate_Volume=" + result.getMinDate());
		context.write(key, result);

    }
}
