package com.me.secondarysort;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.json.JSONException;
import org.json.JSONObject;


public class sortMapper extends Mapper<Object, Text, CompositeKeyWriteable, NullWritable>{
	CompositeKeyWriteable cw=new CompositeKeyWriteable();
	@Override
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String stars;
		String review_count;
		String name;
		String line = value.toString();
		String[] tuple = line.split("\\n");
		try {
			for (int i = 0; i < tuple.length; i++) {
				JSONObject obj = new JSONObject(tuple[i]);
				stars=obj.getString("stars");
				review_count = obj.getString("review_count");
				name=obj.getString("name");
				cw.setReview_count(Integer.parseInt(review_count));
				cw.setStars(stars);
				cw.setName(name);
				context.write(cw, NullWritable.get());
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
