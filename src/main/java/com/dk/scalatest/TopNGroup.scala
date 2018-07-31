package com.dk.scalatest

import org.apache.spark.{SparkConf, SparkContext}

object TopNGroup {
    def main(args: Array[String]): Unit = {
        println("Scala----------------------------------------------------------")
        val conf = new SparkConf().setAppName("ScalaTopNGroup").setMaster("local")
        val sc = new SparkContext(conf)
        val lines = sc.textFile("/Users/rikka/myfile/file/TopNGroup.txt")
        val top5 = lines.map(line => {
            val splitedLine = line.split(" ")
            (splitedLine(0),splitedLine(1).toInt)
        }).groupByKey().map(groupedData => {
            val groupedKey = groupedData._1
            val top5 = groupedData._2.toList.sortWith(_>_).take(5)
            Tuple2(groupedKey,top5)
        })

        top5.foreach(topped => {
            println("Group key : "+topped._1)
            val toppedValue = topped._2.iterator
            while(toppedValue.hasNext){
                val value = toppedValue.next
                println(value)
            }

            println("***********************************")
        })

    }

}
