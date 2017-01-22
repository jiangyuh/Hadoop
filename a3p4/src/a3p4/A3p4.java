/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a3p4;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MultiFileInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FixedLengthInputFormat;

import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author apple
 */
public class A3p4 {

    /**
     * @param args the command line arguments
     */
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            String[] line = value.toString().split("::");

            String gender = line[0];
//            StringTokenizer itr = new StringTokenizer(value.toString());
//                while (itr.hasMoreTokens()) {
            word.set(gender);
            context.write(word, one);
              //  }

        }
    }

    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
    
    
    public static class SequenceFileInputFormatDemoMapper extends
            Mapper<LongWritable, Text, Text, NullWritable> {

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            System.out.println("key:   " + key.toString() + "  ;  value: "
                    + value.toString());
        }
    }
    
   public static class FixedLengthInputFormatDemoMapper extends
            Mapper<LongWritable, BytesWritable,LongWritable , BytesWritable> {
    	@Override
        public void map(LongWritable key,BytesWritable value, Context context)
                throws IOException, InterruptedException {
        	System.out.println("value="+value);
            context.write( new LongWritable(Long.valueOf(5)),value);
        }
    }

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Configuration conf = new Configuration();
        // This is NLineInputFormat
        Job job1 = Job.getInstance(conf, "count");
        job1.setJarByClass(A3p4.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        job1.setInputFormatClass(NLineInputFormat.class);  
        job1.setMapperClass(TokenizerMapper.class);
        job1.setCombinerClass(IntSumReducer.class);
        job1.setReducerClass(IntSumReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        FileOutputFormat.setOutputPath(job1, new Path(args[1]+"NLine"));
        System.exit(job1.waitForCompletion(true) ? 0 : 1);
        
        // This is FixedLengthInputFormat
//        conf.setInt(FixedLengthInputFormat.FIXED_RECORD_LENGTH, 1);
//        Job job2 = Job.getInstance(conf, "count");
//        job2.setJarByClass(A3p4.class);
//        FileInputFormat.addInputPath(job2, new Path(args[0])); 
//        job2.setMapperClass(FixedLengthInputFormatDemoMapper.class);
//        job2.setNumReduceTasks(0);
//        job2.setOutputKeyClass(LongWritable.class);
//        job2.setOutputValueClass(ByteWritable.class);
//        job2.setInputFormatClass(FixedLengthInputFormat.class); 
//        FileOutputFormat.setOutputPath(job2, new Path(args[1]+"Fixed"));
//        System.exit(job2.waitForCompletion(true) ? 0 : 1);        

        // This is KeyValueTextInputFormat
//        Job job3 = Job.getInstance(conf, "count");
//        job3.setJarByClass(A3p4.class);
//        FileInputFormat.addInputPath(job3, new Path(args[0]));
//        job3.setInputFormatClass(KeyValueTextInputFormat.class);  
//        job3.setMapperClass(TokenizerMapper.class);
//        job3.setCombinerClass(IntSumReducer.class);
//        job3.setReducerClass(IntSumReducer.class);
//        job3.setOutputKeyClass(Text.class);
//        job3.setOutputValueClass(IntWritable.class);
//        FileOutputFormat.setOutputPath(job3, new Path(args[1]+"KeyValue"));
//        System.exit(job3.waitForCompletion(true) ? 0 : 1);        

        // This is TextInputFormat
//        Job job4 = Job.getInstance(conf, "count");
//        job4.setJarByClass(A3p4.class);
//        FileInputFormat.addInputPath(job4, new Path(args[0]));
//        job4.setInputFormatClass(TextInputFormat.class);  
//        job4.setMapperClass(TokenizerMapper.class);
//        job4.setCombinerClass(IntSumReducer.class);
//        job4.setReducerClass(IntSumReducer.class);
//        job4.setOutputKeyClass(Text.class);
//        job4.setOutputValueClass(IntWritable.class);
//        FileOutputFormat.setOutputPath(job4, new Path(args[1]+"Text"));
        
        // This is SequenceFileInputFormat
//        Job job5 = Job.getInstance(conf, "count");
//        job5.setJarByClass(A3p4.class);
//        FileInputFormat.addInputPath(job5, new Path(args[0]));
//        job5.setMapperClass(SequenceFileInputFormatDemoMapper.class);
//        job5.setNumReduceTasks(1);
//        job5.setOutputKeyClass(Text.class);
//        job5.setOutputValueClass(NullWritable.class);
//        job5.setMapOutputKeyClass(Text.class);
//        job5.setMapOutputValueClass(Text.class);
//        job5.setInputFormatClass(SequenceFileInputFormat.class);
//        FileOutputFormat.setOutputPath(job5, new Path(args[1]+"SequenceFile"));
//        System.exit(job5.waitForCompletion(true) ? 0 : 1);


        
        // This is MultipleInputFormat
//        Job job6 = Job.getInstance(conf, "count");
//        job6.setJarByClass(A3p4.class);
//        FileInputFormat.addInputPath(job6, new Path(args[0]));
//        job6.setInputFormatClass(TextInputFormat.class);
//        MultipleInputs.addInputPath(job6, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
//	MultipleInputs.addInputPath(job6, new Path(args[1]), TextInputFormat.class, TokenizerMapper.class);
//        //job6.setMapperClass(TokenizerMapper.class);
//        job6.setCombinerClass(IntSumReducer.class);
//        job6.setReducerClass(IntSumReducer.class);
//        job6.setOutputKeyClass(Text.class);
//        job6.setOutputValueClass(IntWritable.class);
//        FileOutputFormat.setOutputPath(job6, new Path(args[2]+"MultiFile"));
//       System.exit(job6.waitForCompletion(true) ? 0 : 1);        




       

    }

}
