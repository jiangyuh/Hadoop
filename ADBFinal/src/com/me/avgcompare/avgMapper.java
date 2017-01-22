package com.me.avgcompare;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONException;
import org.json.JSONObject;

public class avgMapper extends Mapper<Object, Text, Text, SortedMapWritable> {
	private static final DoubleWritable one= new  DoubleWritable(1.0);
	private DoubleWritable volumn=new DoubleWritable(0.0);
	public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
		String city;
		String state;
        String star;
        String line = value.toString();
        String[] tuple = line.split("\\n");
        try{
            for(int i=0;i<tuple.length; i++){
                JSONObject obj = new JSONObject(tuple[i]);
                city=obj.getString("city");
                state=obj.getString("state");
                if(!obj.isNull("city")&&!city.equals("")&&state.length()==2)
                {
                star = obj.getString("stars");
                volumn.set(Double.parseDouble(star));
                SortedMapWritable outLength= new SortedMapWritable();
                outLength.put(volumn, one);
                context.write(new Text(city+","+state), outLength);
                }

            }
            
            
            
            
        }catch(JSONException e){
            e.printStackTrace();
        }
	}

}
