import sbt._
import Keys._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val mockito = "org.mockito" % "mockito-core" % "2.10.0"
  lazy val mockitoInline = "org.mockito" % "mockito-inline" % "2.10.0"

  val commonDependencies: Seq[ModuleID] = Seq(
    //wrupple
    scalaTest % "test",
    mockito % "test",
    mockitoInline % "test",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2"/* % Runtime,*/
  )

  val sparkVersion = "1.6.0"


  val sparkDependencyScope = "provided"


  val apiDependencies    : Seq[ModuleID] = commonDependencies

  val sparkDependencies  : Seq[ModuleID] = commonDependencies ++ Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % sparkDependencyScope,
      "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope,
      "org.apache.spark" %% "spark-hive" % sparkVersion % sparkDependencyScope
//    "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope,
//    "org.apache.spark" %% "spark-mllib" % sparkVersion % sparkDependencyScope,
//    "org.apache.spark" %% "spark-streaming" % sparkVersion % sparkDependencyScope,
  )
  /*val webDependencies    : Seq[ModuleID] = commonDependencies ++ json ++ {
    Seq(
      //jdbc,
      //cache,
      // ws
      //specs2 % Test
    )

  }*/
}
