package com.me.a4p3;
/*Determine the average stock_price_adj_close value by the year.
Decide if a Combiner Optimization is needed.
If yes, implement Combiner.*/

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class a4p3 {

	public static class avgMapper extends Mapper<Object, Text, Text, DoubleWritable> {
		public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
			String s = value.toString();
			StringTokenizer itr = new StringTokenizer(s);
			while (itr.hasMoreTokens()) {
				String s2 = itr.nextToken();
				String a[] = s2.split(",");

				if (!a[2].equals("date") && a.length > 4) {
					String key1 = a[2].split("-")[0];
					double value1 = Double.parseDouble(a[8]);
					context.write(new Text(key1), new DoubleWritable(value1));
				}
			}
		}
	}

	public static class AvgReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		private DoubleWritable result = new DoubleWritable();

		public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
				throws IOException, InterruptedException {
			double sum = 0;
			int count = 0;
			for (DoubleWritable val : values) {
				sum += val.get();
				count++;
			}
			result.set(Math.round(sum / count * 100) / 100.0);
			context.write(key, result);
		}
	}
// calculating median requests all the value and the combiner isn't needed.
	
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "count");
		job.setJarByClass(a4p3.class);
		job.setMapperClass(avgMapper.class);
		job.setReducerClass(AvgReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
