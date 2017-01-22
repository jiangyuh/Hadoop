package com.me.toptenCompare;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONException;
import org.json.JSONObject;

public class ttcMapper extends Mapper<Object, Text, Text, IntWritable>{
	public void map(Object key, Text value, Context context) 
			throws IOException, InterruptedException {
	String review_count;
	String name;
	String line = value.toString();
	String[] tuple = line.split("\\n");
	try {
		for (int i = 0; i < tuple.length; i++) {
			JSONObject obj = new JSONObject(tuple[i]);
			
			review_count = obj.getString("review_count");
			name=obj.getString("name");
			
//			// When size larger
//			review_count=obj.getString("stars");
//			name=obj.getString("user_id");
			
			context.write(new Text(name), new IntWritable(Integer.parseInt(review_count)));
		}
	} catch (JSONException e) {
		e.printStackTrace();
	}
	}
}
