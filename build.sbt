val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "seekprog",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    run / fork := true,
    connectInput := true,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
