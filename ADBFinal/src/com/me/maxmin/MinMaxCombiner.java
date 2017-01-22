package com.me.maxmin;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.me.tuple.MinMaxTuple;

public class MinMaxCombiner extends Reducer<Text,MinMaxTuple, Text,MinMaxTuple> {
	
	 public void reduce(Text key, Iterable<MinMaxTuple> values, Context context) throws IOException, InterruptedException {
		    MinMaxTuple minmax = new  MinMaxTuple();

		    minmax.setMaxPrice(null);
		    minmax.setMinPrice(null);

			double maxPriceRange = 0;
			double minPriceRange = 6;
			for (MinMaxTuple val : values) {
				if (val.getMaxPrice() != null && val.getMinPrice() != null) {
					if (maxPriceRange < Double.parseDouble(val.getMaxPrice())) {
						maxPriceRange=Double.parseDouble(val.getMaxPrice());
						minmax.setMaxPrice(String.valueOf(maxPriceRange));
					
					}
					if ( minPriceRange > Double.parseDouble(val.getMinPrice())) {
						minPriceRange=Double.parseDouble(val.getMinPrice());
						minmax.setMinPrice(String.valueOf(minPriceRange));
					}

				}
			}

			context.write(key, minmax);
 }
}


