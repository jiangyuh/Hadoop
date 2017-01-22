package com.me.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.me.join.CommentJoinMapper;
import com.me.join.UserJoinMapper;
import com.me.join.UserJoinReducer;
import com.me.topten.topTenHBaseReducer;
import com.me.topten.topTenMapper;

public class HbaseUploader {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
// HBase for top ten
		String tablename_tt = "topten";
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "localhost");
		HBaseAdmin admin = new HBaseAdmin(conf);
		if (admin.tableExists(tablename_tt)) {
			System.out.println("table exists!recreating.......");
			admin.disableTable(tablename_tt);
			admin.deleteTable(tablename_tt);
		}
		HTableDescriptor htd = new HTableDescriptor(tablename_tt);
		HColumnDescriptor tcd = new HColumnDescriptor("content");
		htd.addFamily(tcd);// create column family
		admin.createTable(htd);// create table
		Job job_tt = Job.getInstance(conf, "TopTenHbase");
		job_tt.setJarByClass(HbaseUploader.class);
		job_tt.setMapperClass(topTenMapper.class);
		TableMapReduceUtil.initTableReducerJob(tablename_tt, topTenHBaseReducer.class, job_tt);
		job_tt.setOutputKeyClass(NullWritable.class);
		job_tt.setOutputValueClass(Text.class);
		String inputPath = "hdfs://localhost:9000/final/yelp_academic_dataset_business.json";
		FileInputFormat.addInputPath(job_tt, new Path(inputPath));
		

// HBase for join max-min and average
		Configuration conf1 = HBaseConfiguration.create();
		conf1.set("hbase.zookeeper.quorum", "localhost");
		String tablename_amm = "maxminavg";
		HBaseAdmin admin_amm = new HBaseAdmin(conf1);
		if (admin_amm.tableExists(tablename_amm )) {
			System.out.println("table exists!recreating.......");
			admin_amm.disableTable(tablename_amm );
			admin_amm.deleteTable(tablename_amm );
		}
		HTableDescriptor htd_amm = new HTableDescriptor(tablename_amm);
		HColumnDescriptor tcd_amm = new HColumnDescriptor("context");
		//System.out.println("4");
		htd_amm.addFamily(tcd_amm);// create column family
		admin_amm.createTable(htd_amm);// create table

		Job job_amm = Job.getInstance(conf1, "AvgHbase");
		job_amm.setJarByClass(HbaseUploader.class);
		MultipleInputs.addInputPath(job_amm, new Path("hdfs://localhost:9000/final/avg/part-r-00000"), TextInputFormat.class, UserJoinMapper.class);
		MultipleInputs.addInputPath(job_amm, new Path("hdfs://localhost:9000/final/maxmin/part-r-00000"), TextInputFormat.class, CommentJoinMapper.class);
		job_amm.getConfiguration().set("join.type", "inner");
		TableMapReduceUtil.initTableReducerJob(tablename_amm, UserJoinReducer.class, job_amm);
		job_amm.setOutputKeyClass(Text.class);
		job_amm.setOutputValueClass(Text.class);
		
		job_tt.waitForCompletion(true);
		job_amm.waitForCompletion(true);

	}

}
