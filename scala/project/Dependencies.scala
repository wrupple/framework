import sbt._

object Dependencies {

  val sparkVersion = "1.6.0"

  val sparkDependencyScope = "provided"


  lazy val bpm = Wrupple.muba % "muba-bpm" % Wrupple.version
  lazy val hsql = Wrupple.muba % "muba-catalogs-jdbc-hsql" % Wrupple.version
  lazy val choco = Wrupple.muba % "muba-runner-choco" % Wrupple.version
  lazy val bval = Wrupple.muba % "validation-bval" % Wrupple.version

  lazy val guice = "com.google.inject" % "guice" % "4.1.0"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.21"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val mockito = "org.mockito" % "mockito-core" % "2.10.0"
  lazy val mockitoInline = "org.mockito" % "mockito-inline" % "2.10.0"

  val commonDependencies: Seq[ModuleID] = Seq(
    hsql,
    choco,
    bval,
    bpm,
    guice,
    slf4j % "test",
    scalaTest % "test",
    mockito % "test",
    mockitoInline % "test",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
    "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
  )

  val apiDependencies    : Seq[ModuleID] = commonDependencies

  val sparkDependencies  : Seq[ModuleID] = commonDependencies ++ Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % sparkDependencyScope,
      "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope,
      "org.apache.spark" %% "spark-hive" % sparkVersion % sparkDependencyScope
  )

}
