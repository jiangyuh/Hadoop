package com.me.a4p6;
/*
 Find the Distinct IP addresses from the log file provided in the previous assignment. 
 * */
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class a4p6 {
	
	public static class SimpleMapper extends Mapper<Object, Text, Text, NullWritable> {
		
		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {


			if (value.toString().length() > 0) {
				String values[] = value.toString().split("- -");
				context.write(new Text(values[0]), NullWritable.get());
			}

		}

	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "count");
        job.setJarByClass(a4p6.class);
        job.setMapperClass(SimpleMapper.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
