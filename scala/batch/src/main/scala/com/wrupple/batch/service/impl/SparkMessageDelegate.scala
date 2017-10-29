package com.wrupple.batch.service.impl

import com.wrupple.batch.service.BatchMessageDelegate
import com.wrupple.muba.bpm.domain.WorkRequest
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.launcher.SparkAppHandle.Listener
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

class SparkMessageDelegate extends BatchMessageDelegate with Logging {


  override def send(job: WorkRequest) = {
    logger.info(s"using jar ${"file://home/prtrods/testIsban/carlos/ODSbatch.jar"}")

    //https://stackoverflow.com/questions/32095428/move-file-from-local-to-hdfs
    val handle: SparkAppHandle = new SparkLauncher()
      .setAppResource("file:///home/prtrods/testIsban/carlos/ODSbatch.jar")
      //.setAppResource("file:////home/ALTECMEXICO/z563926/workspace/usr/ODS/src/ODSbatch/target/ODSbatch-bundled-0.1.jar")
      .setMainClass("mx.isban.ods.examples.WordCount")
      //.setConf("spark.driver.extraJavaOptions", "-Dlog4j.configuration=file:log4j.properties")
      .setMaster("yarn")
      .setSparkHome("/opt/cloudera/parcels/CDH-5.7.1-1.cdh5.7.1.p0.11/lib/spark")
      .setDeployMode("cluster")
      //.setConf(SparkLauncher.EXECUTOR_MEMORY, "3072")
      //.setConf(SparkLauncher.DRIVER_MEMORY, "2g")
      .addAppArgs(
      "--inputFile=hdfs://mgtdtlktvlmx101.dev.mx.corp:8020/dev/landing/ods/results.csv",
        "--output=hdfs://mgtdtlktvlmx101.dev.mx.corp:8020/dev/landing/ods/",
      "--runner=SparkRunner"
    )
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
