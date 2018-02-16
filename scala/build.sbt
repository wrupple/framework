name := "batch"
organization in ThisBuild := "com.wrupple"
scalaVersion in ThisBuild := "2.10.5"

// PROJECTS

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
      /*dependencies.choco,*/
      dependencies.lambda
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
      dependencies.hive
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val wrupple = "com.wrupple.muba"
    val wruppleVersion = "1.0"
    val sparkVersion = "1.6.0"
    val sparkDependencyScope = "provided"
    val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.21"
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
    val mockito = "org.mockito" % "mockito-core" % "2.10.0"
    val mockitoInline = "org.mockito" % "mockito-inline" % "2.10.0"
    val worker = wrupple % "muba-worker" % wruppleVersion
    val hsql = wrupple % "muba-catalogs-jdbc-hsql" % wruppleVersion
    val choco = wrupple % "muba-runner-choco" % wruppleVersion
    val bval = wrupple % "validation-bval" % wruppleVersion
    val lambda = wrupple % "muba-lambda" % wruppleVersion
    val spark = "org.apache.spark" %% "spark-core" % sparkVersion % sparkDependencyScope
    val launcher = "org.apache.spark" %% "spark-launcher" % "2.2.1"
    //val actor = "com.typesafe.akka" %% "akka-actor" % "2.5.9"
    val sql = "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope
    val hive = "org.apache.spark" %% "spark-hive" % sparkVersion % sparkDependencyScope
    val config = "com.typesafe" % "config" % "1.3.1"
  }

lazy val commonDependencies = Seq(
  dependencies.bval,
  dependencies.config,
  dependencies.mockitoInline % "test",
  dependencies.slf4j % "test",
  dependencies.scalaTest % "test",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2" /* % Runtime,*/
)

// SETTINGS

lazy val settings =
  commonSettings ++
    wartremoverSettings ++
    scalafmtSettings

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

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.mavenCentral,
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

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)
