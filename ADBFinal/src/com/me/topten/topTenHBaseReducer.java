package com.me.topten;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.json.JSONException;
import org.json.JSONObject;

public class topTenHBaseReducer extends TableReducer<NullWritable, Text, ImmutableBytesWritable> {
	
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
			String[] split=t.toString().split(",");
			Put put = new Put(split[0].getBytes());
			put.add(Bytes.toBytes("content"), Bytes.toBytes("count"), Bytes.toBytes(split[1]));
			context.write(new ImmutableBytesWritable(split[0].getBytes()), put);
		}
		

	}
}

	

