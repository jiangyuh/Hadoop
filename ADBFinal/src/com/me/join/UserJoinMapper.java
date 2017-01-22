package com.me.join;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserJoinMapper extends Mapper<Object, Text, Text, Text>{
	private Text outkey = new Text();
	private Text outvalue = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		// Parse the input string into a nice map
		//String[] separatedInput = value.toString().split(regex, -1);
		String[] separatedInput = value.toString().split(",");
		String rname = separatedInput[0];
		if (rname == null) {
			return;
		}
		// The foreign join key is the user ID
		outkey.set(rname);
		// Flag this record for the reducer and then output
		outvalue.set("A" + value.toString());
		context.write(outkey, outvalue);
	}

}
