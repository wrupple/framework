package com.wrupple.batch.service.impl

import com.wrupple.batch.service.BatchMessageDelegate
import com.wrupple.muba.bpm.domain.WorkRequest
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.launcher.SparkAppHandle.Listener
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

class SparkMessageDelegate extends BatchMessageDelegate with Logging {


  override def send(job: WorkRequest) = {
    //https://stackoverflow.com/questions/32095428/move-file-from-local-to-hdfs
    val handle: SparkAppHandle = new SparkLauncher()
      .setAppResource("~/testIsban/carlos/ODSbatch.jar")
      .setMainClass("mx.isban.ods.examples.WordCount")
      .setConf("spark.driver.extraJavaOptions", "-Dlog4j.configuration=file:log4j.properties")
      .setMaster("yarn")
      .setSparkHome("/opt/cloudera/parcels/CDH-5.7.1-1.cdh5.7.1.p0.11/lib/spark/")
      .setDeployMode("client")
      .setConf(SparkLauncher.EXECUTOR_MEMORY, "3072")
      .setConf(SparkLauncher.DRIVER_MEMORY, "2g")
      .addAppArgs(
        "--sparkMaster=yarn",
        "--runner=SparkRunner",
        "--output=hdfs://mgtdtlktvlmx101.dev.mx.corp:8020/dev/landing/ods/",
        "--inputFile=hdfs://mgtdtlktvlmx101.dev.mx.corp:8020/dev/landing/ods/results.csv")
      .startApplication(
        new Listener() {
          override def infoChanged(sparkAppHandle: SparkAppHandle) = {
            logger.info(sparkAppHandle.getState.toString)
          }

          override def stateChanged(sparkAppHandle: SparkAppHandle) = {
            logger.info(sparkAppHandle.getState.toString)
          }
        }
      );

    job.setResult(-1);
    job
  }
}
