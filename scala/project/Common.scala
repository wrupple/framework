import sbt.Keys._
import sbt._

object Common {


  lazy val copyDependencies = TaskKey[Unit]("copy-dependencies")

  def copyDepTask = copyDependencies <<= (update, crossTarget, scalaVersion) map {
    (updateReport, out, scalaVer) =>
    updateReport.allFiles foreach { srcPath =>
      val destPath = out / "lib" / srcPath.getName
      IO.copyFile(srcPath, destPath, preserveLastModified=true)
    }
  }
  
  val settings: Seq[Def.Setting[_]] = Seq(
    version := Wrupple.version,
    organization := Wrupple.muba,
    scalaVersion := "2.10.5",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"), //, "-Xmx2G"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    resolvers += Opts.resolver.mavenLocalFile,
    copyDepTask,
    scalacOptions += "-feature",
    fork in Test := true,
    resolvers ++= Seq(DefaultMavenRepository,
      Resolver.defaultLocal,
      Resolver.mavenLocal,
      // Resolver.mavenLocal has issues - hence the duplication
      "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
      Classpaths.sbtPluginReleases)
  )
}
