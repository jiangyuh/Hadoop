/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a3p2;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

/**
 *
 * @author apple
 */
public class A3p2 {

    /**
     * @param args the command line arguments
     */
//    public static class TokenizerMapper
//            extends MapReduceBase
//            implements Mapper<LongWritable, Text, Text, DoubleWritable> {
//
//        @Override
//        public void map(LongWritable fileOffset, Text lineContents, OutputCollector<Text, DoubleWritable> oc, Reporter rprtr) throws IOException {
////            throw new UnsupportedOperationException("Not supported yet."); 
//            String line = lineContents.toString();
//            String[] parts = line.split("\\::");
//            if (parts.length >= 1) {
//                String key = parts[1];
//                oc.collect(new Text(key), new DoubleWritable(Double.valueOf(parts[2])));
//            }
//        }
//    }
//    public static class MovieReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
//
//        @Override
//        public void reduce(Text movie, Iterator<DoubleWritable> count, OutputCollector<Text, DoubleWritable> oc, Reporter rprtr) throws IOException {
//            int counter = 0;
//            double sum = 0;
//            while (count.hasNext()) {
//                counter++;
//                sum += count.next().get();
//            }
//            DoubleWritable d = new DoubleWritable(Math.floor((sum / counter) * 1000) / 1000);
//            oc.collect(movie, d);
//        }
//    }
//
//    public static class SortReducer extends MapReduceBase implements Reducer<DoubleWritable, Text, DoubleWritable, Text> {
//
//        private static int counter = 0;
//
//        @Override
//        public void reduce(DoubleWritable Object, Iterator<Text> count, OutputCollector<DoubleWritable, Text> oc, Reporter rprtr) throws IOException {
//            while (count.hasNext()) {
//                counter++;
//                if (counter < 25) {
//                    oc.collect(Object, count.next());
//                }
//            }
//        }
//    }
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

    public static class SortMapper extends Mapper<Text, DoubleWritable, Text, DoubleWritable> {

        private static int counter = 0;

        @Override
        public void map(Text Object, DoubleWritable count, Context context)
                throws IOException, InterruptedException {
            
                counter++;
                if (counter < 26) {
                    context.write(Object, count);
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

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Job job = Job.getInstance(new Cluster(new Configuration()));
        ChainMapper chainMapper = new ChainMapper();
        ChainReducer chainReducer = new ChainReducer();
        job.setJarByClass(A3p2.class);
        
        Configuration mapConf1 = new Configuration(false);
        chainMapper.addMapper(job, TokenizerMapper.class, LongWritable.class, Text.class,Text.class, DoubleWritable.class, mapConf1);
        
        Configuration reduceConf1 = new Configuration(false);
        chainReducer.setReducer(job, MovieReducer.class, Text.class, DoubleWritable.class, Text.class, DoubleWritable.class, reduceConf1);
        
        Configuration mapConf2 = new Configuration(false);
        chainReducer.addMapper(job, SortMapper.class, Text.class, DoubleWritable.class, Text.class,DoubleWritable.class, mapConf2);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setSortComparatorClass(DoubleWritableDecreasingComparator.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}
