package com.me.topten;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.JSONException;
import org.json.JSONObject;

public class topTenReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

	private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();

	public void reduce(NullWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		for (Text value : values) {
			String review_count;
			String name;
			String line = value.toString();
			String[] tuple = line.split("\\n");
			try {
				for (int i = 0; i < tuple.length; i++) {
					JSONObject obj = new JSONObject(tuple[i]);
					
					review_count = obj.getString("review_count");
					name=obj.getString("name");
					
//					// When size larger
//					review_count=obj.getString("stars");
//					name=obj.getString("user_id");

					repToRecordMap.put(Integer.parseInt(review_count), new Text(name+","+review_count));

				}
				// If we have more than ten records, remove the one with the lowest
				// As this tree map is sorted in descending order, the user with
				// the review_count is the last key.
				if (repToRecordMap.size() > 10) {
					repToRecordMap.remove(repToRecordMap.firstKey());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		for (Text t : repToRecordMap.descendingMap().values()) {
			// Output our ten records to the file system with a null key
			context.write(NullWritable.get(), t);
		}
	}
}
