# Hadoop
Code in Folders
a2p1 calculate the average price of different stocks

part2 get the top25 rating movies

part3 calculate how many people are male and female

part4 calculate how many different movies

part5 calculate how many different ips in the file

a3p1 merge a lot of files from local into one file and put the file in HDFS and calculate the average price of different stocks

a3p2 use chainMapper and chainReducer to figure out the top25 rating movies

a3p4 Use six different FileInputFormat and show how they works

#####HW4

a4p2. Use compositeKey to find the maximum and minumum price of different stock by dates

a4p3. Determine the average stock price value by the year.

a4p4. Using the moviLens dataset, determine the median and standard deviation of comments per movie. 

a4p5. Using the moviLens dataset, determine the median and standard deviation of comments per movie. (Use Memory-Conscious Method)

a4p6. Use Distinct pattern to find distinct ips.

#Final Project Report

##MapReduce Algorithm Based on Yelp Dataset

###Summary：

#####Dataset: [yelp_dataset_challenge_academic_dataset](https://www.yelp.com/academic_dataset)

The project is MapReduce Algorithm Based on Yelp Dataset. It use yelp academic dataset to perform some analysis and compare one algorithm with another normal method. The algorithms perform some analysis in this project below.

1. Find out the maximum star and minimum star in one city. 
(MapReduce Summarization Patterns)

2. Calculate the average stars and show the number of businesses in different cities. 
(MapReduce Summarization Patterns)


3. Show the top ten businesses with the highest review count.
(TopN Filter)

4. Join the Algorithm 1 and Algorithm 2 by cities.
(MapReduce Join Pattern)

5. Show in the same stars, sort the businesses by popularity. 
(Secondary Sorting) 

6. Use another methods to show the efficiency of Top Ten Filter and the efficiency of how to calculate average.

7. Put the result of top ten filter and join pattern in HBase.

###Code Details

####1.Find out Maximum and Minimum:

In every mapper, generate key-value pair. The key is the city name, and the value is stars. And then, emit the key-value pairs emits combiner, which find out the maximum and minimum of each mapper. Emit the maximum and minimum value and then find out maximum and minimum in the reducer to improve the efficiency of Reducer.

#####Combiner and Reducer(use the same code): 
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/1.png)

#####Result: (Data Structure: city,state-maximum stars-minimum stars)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/2.png)

####2. Calculate the Average:

The method is to use Memory-Conscious to calculate the mean. Firstly, the mapper generate the key-value pair, in which city is the key and stars of businesses is the value. And then put them in SortedMapWritable. The number of each record is one. And in combiner, it will compute how many of the same records, and put them into a new SortedMapWritable as well. And the reducer use the count multiples its values to get the sum.

#####Mapper:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/3.png)

#####Reducer:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/4.png)

#####Result: (Data Structure: city,state-average stars-business count)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/5.png)

####3. Find out Top Ten:

In Mapper, put the review_count of each business as key and the records of each business as the value in TreeMap. TreeMap will sort them by Key in descending order. And emit all key-value into Reducer. Reducer repeats the same steps as Mapper and find out the final result of top ten popular businesses.

#####Put the record in TreeMap get the top ten:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/6.png)

Put the output in HBase:

In Reducer, set which part of data should be row and columns. Key is the row family, and “content” is the column family in the table and “count” is the review count of each businesses. 
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/7.png)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/8.png)

#####Result:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/9.png)

####4.  Join the Max-Min and Average values:

Generate key-value pair in two mappers and add a mark to each list and inner join the two files and put the result in HBase Table.

#####Identity which the list is the values is, and put them in different lists:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/10.png)

#####Inner Join two lists and put the each records in columns.
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/11.png)

#####Put the result in HBase.
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/12.png)

#####Result: (In HBase)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/13.png)

####5.Secondary Sorting:

This use a composite key that contains both the information needed to sort by key and the information needed by value, and then decoupling the grouping of the intermediate data from the sorting of the intermediate data. Partitioner divide the keys by their hash code, and emit the value with the same key to one reducer. The method use business name, business stars and business review count. The composite key contains stars and review_count. First, sort the stars in descending order, if the stars are the same, sort review_count in descending order. 

It will show in different stars, which are more popular business.

#####Composite Key:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/14.png)

#####Mapper:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/15.png)

#####Partitioner:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/16.png)

#####Comparator:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/17.png)

#####Reducer:
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/18.png)

###The Efficiency of Finding Out the Top Ten Businesses Methods

####1. Use top ten Filter (Code showed above)

####2. Use two reducer, the first one generate the city and the review count pairs and the second reducer to output the top ten records generated by the first reducer. And in this method, it used IntWritable.Comparator function.
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/21.png)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/22.png)

Compare the size of files are 77.3 MB and 2.29GB

The Chart shows that when the file size is small, the efficiency of these two methods differ a little, but the file size grows, the time of sorting and outputting top10 records will increase obviously. The time of two reducer to sort and output will cost more than get top ten from each mapper, because in the top ten filter, the reducer will process less data than the other methods, which will process all data in reducer. It improves the efficiency of the reducer and it improve the performance of the methods.

![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/19.png)

###The Efficiency of Calculating Mean

The regular method is to sum up all value and get the number of the values and then to get the mean of the dataset.
The other method is to use Memory-Conscious to calculate the mean. Firstly, the mapper generate the key-value pair, in which city is the key and stars of businesses is the value. And then put them in SortedMapWritable. The number of each record is one. And in combiner, it will compute how many of the same records, and put them into a new SortedMapWritable as well. And the reducer use the count multiples its values to get the sum. And this methods will reduce the size of data emitted into reducer and increase the efficiency of computation.

![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/23.png)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/24.png)
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/25.png)

The time cost of these two methods shows below.
![alt tag](https://github.com/jiangyuh/Hadoop/blob/master/Code%20Sample%20Pic/20.png)
