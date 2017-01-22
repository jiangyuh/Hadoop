/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a3p1;

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

/**
 *
 * @author apple
 */
public class A3p1 {

    /**
     * @param args the command line arguments
     */
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, DoubleWritable> {

        public void map(Object key, Text value, Mapper.Context context
        ) throws IOException, InterruptedException {
            String s = value.toString();
            StringTokenizer itr = new StringTokenizer(s);
            while (itr.hasMoreTokens()) {
                String s2 = itr.nextToken();

                String a[] = s2.split(",");
                if (!a[1].equals("stock_symbol") && a.length > 4) {
                    
                    String key1 = a[1];
                    double value1 = Double.parseDouble(a[4]);
                    context.write(new Text(key1), new DoubleWritable(value1));
                }
            }
        }
    }

    public static class SumReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();

        public void reduce(Text key, Iterable<DoubleWritable> values,
                Context context
        ) throws IOException, InterruptedException {

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

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Configuration conf = new Configuration();
        //String[] arg = {"hdfs://localhost:9000/apple/NYSE", "hdfs://localhost:9000/apple/output/sum.csv","hdfs://localhost:9000/apple/output"};
        FileSystem local = FileSystem.getLocal(conf);
        Path inputDir = new Path(args[0]);
        Path hdfsFile = new Path(args[1]);
        try {
            FileStatus[] inputFiles = local.listStatus(inputDir);
            FileSystem hdfs = hdfsFile.getFileSystem(conf);
            FSDataOutputStream out = hdfs.create(hdfsFile);
            for (int i = 0; i < inputFiles.length; i++) {
                System.out.println(inputFiles[i].getPath().getName());
                FSDataInputStream in = local.open(inputFiles[i].getPath());
                byte buffer[] = new byte[256];
                int bytesRead = 0;
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                in.close();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("error:: " + e.getMessage());
            e.printStackTrace();
        }
        Job job = Job.getInstance(conf, "count");
        job.setJarByClass(A3p1.class);
        long starTime=System.currentTimeMillis();
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(SumReducer.class);
        job.setReducerClass(SumReducer.class);
        long endTime=System.currentTimeMillis();
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        long timeDiff=endTime-starTime;
        System.out.println("Time Difference:" + timeDiff);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
