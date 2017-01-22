package com.me.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.me.avg.AvgStarReducer;
import com.me.maxmin.MinMaxCombiner;
import com.me.maxmin.MinMaxReducer;
import com.me.secondarysort.CompositeKeyWriteable;
import com.me.secondarysort.sortGroupingComparator;
import com.me.secondarysort.sortMapper;
import com.me.secondarysort.sortPartitioner;
import com.me.secondarysort.sortReducer;
import com.me.avg.AvgStarMapper;
import com.me.maxmin.MinMaxMapper;
import com.me.tuple.AvgTuple;
import com.me.tuple.MinMaxTuple;

public class MainFunc {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String inputPath = "hdfs://localhost:9000/final/yelp_academic_dataset_business.json";

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Job job_avg = Job.getInstance(conf, "Average");
		job_avg.setMapperClass(AvgStarMapper.class);
		job_avg.setReducerClass(AvgStarReducer.class);
		job_avg.setMapOutputKeyClass(Text.class);
		job_avg.setMapOutputValueClass(DoubleWritable.class);
		job_avg.setOutputKeyClass(Text.class);
		job_avg.setOutputValueClass(AvgTuple.class);
		Path p2 = new Path("hdfs://localhost:9000/final/avg");
		if (fs.exists(p2)) {
			fs.delete(p2, true);
		}
		FileInputFormat.addInputPath(job_avg, new Path(inputPath));
		FileOutputFormat.setOutputPath(job_avg, new Path("hdfs://localhost:9000/final/avg"));

		Job job_mm = Job.getInstance(conf, "MaxMin");
		job_mm.setMapperClass(MinMaxMapper.class);
		job_mm.setCombinerClass(MinMaxCombiner.class);
		job_mm.setReducerClass(MinMaxReducer.class);
		job_mm.setOutputKeyClass(Text.class);
		job_mm.setOutputValueClass(MinMaxTuple.class);
		Path p3 = new Path("hdfs://localhost:9000/final/maxmin");
		if (fs.exists(p3)) {
			fs.delete(p3, true);
		}
		FileInputFormat.addInputPath(job_mm, new Path(inputPath));
		FileOutputFormat.setOutputPath(job_mm, new Path("hdfs://localhost:9000/final/maxmin"));

		Job job_sort = Job.getInstance(conf, "Sorting");
		job_sort.setJarByClass(MainFunc.class);
		job_sort.setMapperClass(sortMapper.class);
		job_sort.setMapOutputKeyClass(CompositeKeyWriteable.class);
		job_sort.setMapOutputValueClass(NullWritable.class);
		job_sort.setPartitionerClass(sortPartitioner.class);
		job_sort.setGroupingComparatorClass(sortGroupingComparator.class);
		job_sort.setReducerClass(sortReducer.class);
		job_sort.setOutputKeyClass(CompositeKeyWriteable.class);
		job_sort.setOutputValueClass(NullWritable.class);
		job_sort.setNumReduceTasks(1);
		Path p_join = new Path("hdfs://localhost:9000/final/sort");
		if (fs.exists(p_join)) {
			fs.delete(p_join, true);
		}
		FileInputFormat.addInputPath(job_sort, new Path(inputPath));
		FileOutputFormat.setOutputPath(job_sort, new Path("hdfs://localhost:9000/final/sort"));

		job_avg.waitForCompletion(true);
		job_mm.waitForCompletion(true);
		job_sort.waitForCompletion(true);

	}

}
