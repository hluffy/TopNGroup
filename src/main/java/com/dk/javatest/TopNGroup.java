package com.dk.javatest;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class TopNGroup {
	public static void main(String[] args){
		System.out.println("Java-----------------------------------------------");
//		创建Spark配置对象
		SparkConf sparkConf = new SparkConf().setAppName("Java TopNGroup").setMaster("local");

//		创建SparkContext对象
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);

		JavaRDD<String> lines = ctx.textFile("/Users/rikka/myfile/file/TopNGroup.txt");

//		把每行数据编程符合要求的Key-Value形式
		JavaPairRDD<String,Integer> pairs = lines.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String line){
				String[] splitedLine = line.split(" ");
				return new Tuple2<String,Integer>(splitedLine[0],Integer.valueOf(splitedLine[1]));
			}
		});

		JavaPairRDD<String,Iterable<Integer>> groupedPairs = pairs.groupByKey();

		final JavaPairRDD<String,Iterable<Integer>> top5 = groupedPairs.mapToPair(new PairFunction<Tuple2<String, Iterable<Integer>>, String, Iterable<Integer>>() {
			@Override
			public Tuple2<String, Iterable<Integer>> call(Tuple2<String, Iterable<Integer>> groupedData) throws Exception {
				Integer[] top5 = new Integer[5];	//保存Top5的数据
				String groupedKey = groupedData._1();	//获取分组的组名Key
				Iterator<Integer> groupValue = groupedData._2().iterator();	//获取每组的内容集合
				while(groupValue.hasNext()){	//查看是否有下一个元素，如果有则继续进行循环
					Integer value = groupValue.next();	//获取当前循环的元素本身内容value
					for(int i=0;i<5;i++){
						if(top5[i]==null){
							top5[i] = value;
							break;
						}else if(value > top5[i]){
							for(int j=4;j<i;j--){
								top5[j] = top5[j-1];
							}
							top5[i] = value;
							break;
						}
					}
				}
				return new Tuple2<String,Iterable<Integer>>(groupedKey, Arrays.asList(top5));
			}
		});

//		打印分组后的TopN
		top5.foreach(new VoidFunction<Tuple2<String, Iterable<Integer>>>() {
			@Override
			public void call(Tuple2<String, Iterable<Integer>> topped) throws Exception {
				System.out.println("Group key : "+topped._1());
				Iterator<Integer> toppedValue = topped._2().iterator();
				while(toppedValue.hasNext()){
					Integer value = toppedValue.next();
					System.out.println(value);
				}

				System.out.println("*******************************");

			}
		});

//		关闭JavaSparkContext
		ctx.stop();
	}
}
