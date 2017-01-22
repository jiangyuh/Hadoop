package com.me.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import com.me.avg.AvgStarMapper;
import com.me.avg.AvgStarReducer;
import com.me.avgcompare.avgCombiner;
import com.me.avgcompare.avgMapper;
import com.me.avgcompare.avgReducer;
import com.me.topten.topTenMapper;
import com.me.topten.topTenReducer;
import com.me.toptenCompare.ttcMapper;
import com.me.toptenCompare.ttcReducer;
import com.me.tuple.AvgTuple;
import com.me.toptenCompare.DecreasingComparator;
import com.me.toptenCompare.SortReducer;

public class AlgorithmnCompare {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String inputPath="hdfs://localhost:9000/final/yelp_academic_dataset_business.json";
		//String inputPath="hdfs://localhost:9000/final/yelp_academic_dataset_review.json";
		Configuration conf = new Configuration();
		
		//Top Ten Filter
		long startTime1=System.currentTimeMillis();
		Job job1 = Job.getInstance(conf, "Top10");
		job1.setJarByClass(AlgorithmnCompare.class);
		job1.setMapperClass(topTenMapper.class);
		job1.setReducerClass(topTenReducer.class);
		job1.setOutputKeyClass(NullWritable.class);
		job1.setOutputValueClass(Text.class);
		FileSystem fs=FileSystem.get(conf);
		Path p1=new Path("hdfs://localhost:9000/final/topten/filter");
		if(fs.exists(p1))
		{
			fs.delete(p1,true);
		}
		FileInputFormat.addInputPath(job1, new Path( inputPath));
		FileOutputFormat.setOutputPath(job1, new Path("hdfs://localhost:9000/final/topten/filter"));
		job1.waitForCompletion(true);
		long endTime1=System.currentTimeMillis();
		
		
		// Normal Method to Find Top Ten
		long startTime2=System.currentTimeMillis();
		Job job2 = new Job(conf, "Normal");
        job2.setJarByClass(AlgorithmnCompare.class);
        job2.setMapperClass(ttcMapper.class);
        job2.setReducerClass(ttcReducer.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job2, new Path(inputPath));
        Path p3=new Path("hdfs://localhost:9000/final/topten/compare/");
		if(fs.exists(p3))
		{
			fs.delete(p3,true);
		}
        FileOutputFormat.setOutputPath(job2, new Path("hdfs://localhost:9000/final/topten/compare"));
        job2.setOutputFormatClass(SequenceFileOutputFormat.class);
        job2.waitForCompletion(true);

        //2nd job
        Job jobsort = new Job(conf);
        jobsort.setJarByClass(AlgorithmnCompare.class);
        
        FileInputFormat.addInputPath(jobsort, new Path("hdfs://localhost:9000/final/topten/compare/part-r-00000"));
        jobsort.setInputFormatClass(SequenceFileInputFormat.class);
        jobsort.setMapperClass(InverseMapper.class);
        jobsort.setReducerClass(SortReducer.class);
        jobsort.setOutputKeyClass(IntWritable.class);
        jobsort.setOutputValueClass(Text.class);
        jobsort.setNumReduceTasks(1);
        Path p2=new Path("hdfs://localhost:9000/final/topten/compare/output");
		if(fs.exists(p2))
		{
			fs.delete(p2,true);
		}
        FileOutputFormat.setOutputPath(jobsort, new Path("hdfs://localhost:9000/final/topten/compare/output"));
        jobsort.setSortComparatorClass(DecreasingComparator.class);
        jobsort.waitForCompletion(true);
        long endTime2=System.currentTimeMillis();
        
        //Compare Average
        long startTime3=System.currentTimeMillis();
		Job job_avg = Job.getInstance(conf, "Average");
		job_avg.setJarByClass(AlgorithmnCompare.class);
		job_avg.setMapperClass(AvgStarMapper.class);
		job_avg.setReducerClass(AvgStarReducer.class);
		job_avg.setMapOutputKeyClass(Text.class);
		job_avg.setMapOutputValueClass(DoubleWritable.class);
		job_avg.setOutputKeyClass(Text.class);
		job_avg.setOutputValueClass(DoubleWritable.class);
		Path p4 = new Path("hdfs://localhost:9000/final/avgcompare/1");
		if (fs.exists(p4)) {
			fs.delete(p4, true);
		}
		FileInputFormat.addInputPath(job_avg, new Path(inputPath));
		FileOutputFormat.setOutputPath(job_avg, new Path("hdfs://localhost:9000/final/avgcompare/1"));
		job_avg.waitForCompletion(true);
		long endTime3=System.currentTimeMillis();
		
		//Memory-Conscious
		long startTime4=System.currentTimeMillis();
		Job job = new Job(conf,"MedianStdDev");
		job.setJarByClass(AlgorithmnCompare.class);
		job.setMapperClass(avgMapper.class);
		job.setCombinerClass(avgCombiner.class);
		job.setReducerClass(avgReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(SortedMapWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		Path p5 = new Path("hdfs://localhost:9000/final/avgcompare/2");
		if (fs.exists(p5)) {
			fs.delete(p5, true);
		}
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/final/avgcompare/2"));
		job.waitForCompletion(true);
		long endTime4=System.currentTimeMillis();
		
		
        
        System.out.println("topTen Filter: "+(endTime1-startTime1)+" ms");
        System.out.println("Sort Ten : "+(endTime2-startTime2)+" ms");
        System.out.println("Regular Avg : "+(endTime3-startTime3)+" ms");
        System.out.println("Memory-Conscious Avg : "+(endTime4-startTime4)+" ms");

	}

}
