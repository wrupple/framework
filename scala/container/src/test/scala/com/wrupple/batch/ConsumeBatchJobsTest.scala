package com.wrupple.batch

import org.apache.spark.launcher.SparkLauncher

object ConsumeBatchJobsTest  {
  def main(args: Array[String]): Unit = {
    val sparkLauncher = new SparkLauncher
    //Set Spark properties.only Basic ones are shown here.It will be overridden if properties are set in Main class.
    sparkLauncher.setSparkHome("/path/to/SPARK_HOME")
      .setAppResource("/path/to/jar/to/be/executed")
      .setMainClass("MainClassName")
      .setMaster("MasterType like yarn or local[*]")
      .setDeployMode("set deploy mode like cluster")
      .setConf("spark.executor.cores","2")

    // Lauch spark application
    val sparkLauncher1 = sparkLauncher.startApplication()

    //get jobId
    val jobAppId = sparkLauncher1.getAppId

    //Get status of job launched.THis loop will continuely show statuses like RUNNING,SUBMITED etc.
    while (true) {
      println(sparkLauncher1.getState().toString)
    }
  }

  import java.util
  import java.util.Collections

  def printPairs(arr: Array[Int], n: Int): Unit = {
    val v = new util.ArrayList[Integer]
    val cnt = new util.HashMap[Integer, Integer]
    // For each element of array.
    var i = 0
    while ( {
      i < n
    }) { // If element has not encounter early,
      // mark it on cnt array.
      if (cnt.containsKey(Math.abs(arr(i)))) cnt.put(Math.abs(arr(i)), 1)
      else { // If seen before, push it in vector
        // (given that elements are distinct)
        v.add(Math.abs(arr(i)))
        cnt.put(Math.abs(arr(i)), 0)
      }

      {
        i += 1; 
      }
    }
    if (v.size == 0) return
    Collections.sort(v)
    var i = 0
    while ( {
      i < v.size
    }) System.out.print("-" + v.get(i) + " " + v.get(i) + " ") {
      i += 1; i - 1
    }
  }

}
