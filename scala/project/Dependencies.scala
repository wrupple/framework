import sbt._

object Dependencies {


  lazy val bpm = Wrupple.muba % "muba-bpm" % Wrupple.version
  lazy val sql = Wrupple.muba % "muba-catalogs-sql" % Wrupple.version
  lazy val hsql = Wrupple.muba % "muba-catalogs-jdbc-hsql" % Wrupple.version
  lazy val choco = Wrupple.muba % "muba-runner-choco" % Wrupple.version
  lazy val bval = Wrupple.muba % "validation-bval" % Wrupple.version

  lazy val guice = "com.google.inject" % "guice" % "4.1.0"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.21"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val mockito = "org.mockito" % "mockito-core" % "2.10.0"
  lazy val mockitoInline = "org.mockito" % "mockito-inline" % "2.10.0"

  val commonDependencies: Seq[ModuleID] = Seq(
    bpm,
    guice,
    slf4j % "test",
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
    sql,
    hsql,
    choco,
    bval,
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
<dependency>
			<groupId>com.wrupple.muba</groupId>
			<artifactId>muba-catalogs-jdbc-hsql</artifactId>
			<version>${muba.version}</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.wrupple.muba</groupId>
			<artifactId>muba-runner-choco</artifactId>
			<version>${muba.version}</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.wrupple.muba</groupId>
			<artifactId>validation-bval</artifactId>
			<version>${muba.version}</version>
			<scope>test</scope>
		</dependency>
  }*/
}
