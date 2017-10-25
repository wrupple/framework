name := Wrupple.NamePrefix + "root"


lazy val common = project.
    settings(Common.settings: _*)

lazy val api = project.
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.apiDependencies)

lazy val spark = project.
    dependsOn(api, common).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.sparkDependencies)

lazy val root = (project in file(".")).
    aggregate(api, common, spark)
