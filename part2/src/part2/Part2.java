/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package part2;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
/**
 *
 * @author apple
 */
public class Part2 {

    /**
     * @param args the command line arguments
     */
     public static class TokenizerMapper
            extends Mapper<LongWritable, Text, Text, DoubleWritable> {

        @Override
        public void map(LongWritable fileOffset, Text lineContents, Context context)
                throws IOException, InterruptedException {
            String line = lineContents.toString();
            String[] parts = line.split("\\::");
            if (parts.length >= 1) {
                String key = parts[1];
                context.write(new Text(key), new DoubleWritable(Double.valueOf(parts[2])));
            }
        }
    }
     
     public static class MovieReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        
        @Override
        public void reduce(Text movie, Iterable<DoubleWritable> count, Context context)
                throws IOException, InterruptedException {
            int counter = 0;
            double sum = 0;
            for (DoubleWritable i : count) {
                counter++;
                sum += i.get();
            }
            DoubleWritable d = new DoubleWritable(Math.floor((sum / counter) * 1000) / 1000);
            context.write(movie, d);
        }
    }
     
      public static class SortReducer extends Reducer<DoubleWritable, Text, DoubleWritable, Text> {
        
        private static int counter = 0;
        
        @Override
        public void reduce(DoubleWritable Object, Iterable<Text> count, Context context)
                throws IOException, InterruptedException {
            for (Text c : count) {
                counter++;
                if (counter < 26) {
                    context.write(Object, c);
                }
            }
        }
    }
     
      private static class DoubleWritableDecreasingComparator extends DoubleWritable.Comparator {
        
        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }
        
        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }
      
      
    public static void main(String[] args)throws Exception {
        // TODO code application logic here
        Path p = new Path("/output/" + args[1]);
        Configuration conf = new Configuration();
        Job job = new Job(conf, "MovieRate");
        job.setJarByClass(Part2.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(MovieReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path( args[0]));
        FileOutputFormat.setOutputPath(job, new Path("/output/" + args[1]));
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.waitForCompletion(true);

        //2nd job
        Job jobsort = new Job(conf);
        jobsort.setJarByClass(Part2.class);
        
        FileInputFormat.addInputPath(jobsort, new Path("/output/" + args[1]));
        jobsort.setInputFormatClass(SequenceFileInputFormat.class);
        jobsort.setOutputKeyClass(DoubleWritable.class);
        jobsort.setOutputValueClass(Text.class);
        jobsort.setMapperClass(InverseMapper.class);
        jobsort.setReducerClass(SortReducer.class);
        jobsort.setNumReduceTasks(1);
        FileOutputFormat.setOutputPath(jobsort, new Path("/output/output"));
        jobsort.setSortComparatorClass(DoubleWritableDecreasingComparator.class);
        jobsort.waitForCompletion(true);
    }
    
}
