package com.me.a4p2;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class a4p2 {
	

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "count");
        job.setJarByClass(a4p2.class);
        job.setMapperClass(MinMaxMapper.class);
        job.setCombinerClass(MinMaxCombiner.class);
        job.setReducerClass(MinMaxReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(MinMaxTuple.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
	}


