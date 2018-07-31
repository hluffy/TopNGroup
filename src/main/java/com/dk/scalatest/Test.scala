package com.dk.scalatest

object Test {
    def main(args: Array[String]): Unit = {
        val data = List(3,5,1,7,9,4,3,5)
        println(data.sortWith(_>_).take(4))
    }

}
