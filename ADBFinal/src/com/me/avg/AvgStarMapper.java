package com.me.avg;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.json.JSONException;
import org.json.JSONObject;

public class AvgStarMapper extends Mapper<Object, Text, Text, DoubleWritable>{
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		
		
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
                context.write(new Text(city+","+state), new DoubleWritable(Double.parseDouble(star)));
                }
//                JSONObject attributes =obj.getJSONObject("attributes");
//                if(!attributes.isNull("Price Range"))
//                {
//                price=attributes.getString("Price Range");
//            	context.write(new Text(stars), new DoubleWritable(Double.parseDouble(price)));
//                }
               
            }
            
            
            
            
        }catch(JSONException e){
            e.printStackTrace();
        }
	}

}
