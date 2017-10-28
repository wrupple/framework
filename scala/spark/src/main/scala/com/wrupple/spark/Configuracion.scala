package com.wrupple.spark

import java.io.{InputStream, OutputStream}

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.wrupple.muba.bpm.server.service.VariableConsensus
import com.wrupple.muba.bpm.server.service.impl.ArbitraryDesicion

object Configuracion extends AbstractModule {
  override def configure() = {
    bind(classOf[Boolean]).annotatedWith(Names.named("event.parallel")).toInstance(true)
    bind(classOf[String]).annotatedWith(Names.named("host")).toInstance("localhost")
    bind(classOf[VariableConsensus]).to(classOf[ArbitraryDesicion])
    bind(classOf[OutputStream]).annotatedWith(Names.named("System.out")).toInstance(System.out)
    bind(classOf[InputStream]).annotatedWith(Names.named("System.in")).toInstance(System.in)
    /* val sparkConf = new SparkConf().setAppName(s"ODSCli[${Thread.currentThread().getId}]")
     //https://stackoverflow.com/questions/36021190/run-scala-spark-with-sbt
     //https://github.com/saurfang/sbt-spark-submit
     //sparkConf.setMaster("yarn-client")
     //sparkConf set("spark.driver.host", "your master host ip here")
     val sc = new SparkContext(sparkConf)
     //implicit val fs = FileSystem.get(sc.hadoopConfiguration)
     bind(classOf[SQLContext]).toInstance(new HiveContext(sc))*/

    /*
    import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext


val master: String = "spark://IP:7077"//set IP address to that of your master

val appName: String = "Name of your Application Here"
val conf: SparkConf = new SparkConf().setAppName(appName).setMaster(master);
val sc: JavaSparkContext = new JavaSparkContext(conf)
     */
  }
}
