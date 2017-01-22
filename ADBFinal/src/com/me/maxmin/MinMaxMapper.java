package com.me.maxmin;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.me.tuple.MinMaxTuple;


public class MinMaxMapper extends Mapper<Object, Text, Text, MinMaxTuple> {
	
	private MinMaxTuple mmt = new MinMaxTuple();
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
//                JSONObject attributes =obj.getJSONObject("attributes");
//                if(!attributes.isNull("Price Range"))
//                {
//                price=attributes.getString("Price Range");
                if(!obj.isNull("city")&&!city.equals("")&&state.length()==2)
                {
                
                star=obj.getString("stars");
                mmt.setMaxPrice(star);
                mmt.setMinPrice(star);
                context.write(new Text(city+","+state), mmt);
                }
//                }
               
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
 } 
}


