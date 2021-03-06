lazy val scala210 = "2.10.7"
lazy val scala211= "2.11.12"
ThisBuild / name := "batch"
ThisBuild / organization := "com.wrupple"
ThisBuild / version  := "1.0"
ThisBuild / scalaVersion := scala211
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
// PROJECTS /*, "2.12.2"*/

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    container,
    spark
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val container = project
  .settings(
    name := "container",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.worker,
      dependencies.hsql,
      dependencies.launcher,
      dependencies.choco,
      dependencies.lambda,
      dependencies.remoteCatalogs,
      dependencies.dumbRunner
    )
  )
  .dependsOn(
    common
  )

lazy val spark = project
  .settings(
    name := "spark",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.spark,
      dependencies.sql,
      //dependencies.hive,
      dependencies.remoteCatalogs
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val muba = "com.wrupple.muba"
    val wruppleVersion = "1.0"
    val sparkVersion = "2.3.2"
    val sparkDependencyScope = "provided"
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
    val mockito = "org.mockito" % "mockito-core" % "2.10.0"
    val mockitoInline = "org.mockito" % "mockito-inline" % "2.10.0"
    val worker = muba % "muba-worker" % wruppleVersion
    val hsql = muba % "muba-catalogs-jdbc-hsql" % wruppleVersion
    val choco = muba % "muba-runner-choco" % wruppleVersion
    val bval = muba % "validation-bval" % wruppleVersion
    val lambda = muba % "muba-lambda" % wruppleVersion
    val dumbRunner = muba % "muba-runner-catalog" % wruppleVersion
    val remoteCatalogs = muba % "vegetate-catalogs" % wruppleVersion

    val spark = "org.apache.spark" %% "spark-core" % sparkVersion % sparkDependencyScope
    //https://spark.apache.org/docs/2.3.0/api/java/index.html?org/apache/spark/launcher/package-summary.html
    val launcher = "org.apache.spark" %% "spark-launcher" % sparkVersion
    val sql = "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope
    val hive = "org.apache.spark" %% "spark-hive" % sparkVersion % sparkDependencyScope
  }

lazy val commonDependencies = Seq(
  dependencies.bval,
  dependencies.mockitoInline % "test",
  dependencies.scalaTest % "test"
)

// SETTINGS

lazy val settings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    DefaultMavenRepository,
    //Resolver.mavenCentral,
    Resolver.defaultLocal,
    Resolver.mavenLocal,
    // Resolver.mavenLocal has issues - hence the duplication
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
    "Spray Repository" at "http://repo.spray.cc/",
    "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
    "Akka Repository" at "http://repo.akka.io/releases/",
    "Twitter4J Repository" at "http://twitter4j.org/maven2/",
    "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
    "Twitter Maven Repo" at "http://maven.twttr.com/",
    "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
    "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
    Classpaths.sbtPluginReleases,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)