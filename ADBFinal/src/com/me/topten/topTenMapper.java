package com.me.topten;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONException;
import org.json.JSONObject;

public class topTenMapper extends Mapper<Object, Text, NullWritable, Text> {

	private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

		String review_count;
		String line = value.toString();
		String[] tuple = line.split("\\n");
		try {
			for (int i = 0; i < tuple.length; i++) {
				JSONObject obj = new JSONObject(tuple[i]);
				review_count = obj.getString("review_count");
//				// When size larger
//				review_count=obj.getString("stars");
				
				repToRecordMap.put(Integer.parseInt(review_count), new Text(value));

			}
			// If we have more than ten records, remove the one with the lowest
			// rep
			// As this tree map is sorted in descending order, the user with
			// the review_count is the last key.
			if (repToRecordMap.size() > 10) {
				repToRecordMap.remove(repToRecordMap.firstKey());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void cleanup(Context context) throws IOException, InterruptedException {
		// Output our ten records to the reducers with a null key
		for (Text t : repToRecordMap.values()) {
			context.write(NullWritable.get(), t);
		}
	}

}
