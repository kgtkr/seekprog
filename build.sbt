val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "seekprog",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    run / fork := true,
    connectInput := true,
    Compile / unmanagedJars ++= Attributed.blankSeq(
      IO.read(file("cache/classpath.txt"))
        .split(":")
        .map(name => baseDirectory.value / name.trim)
    ),
    bgCopyClasspath := false,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalafx" %% "scalafx" % "20.0.0-R31"
    ),
    assembly / mainClass := Some("net.kgtkr.seekprog.Main"),
    assembly / assemblyExcludedJars := {
      val cp = (assembly / fullClasspath).value
      val base = (assembly / baseDirectory).value.toPath
      cp.filter(jar => jar.data.toPath.startsWith(base.resolve("cache")))
    },
    scalacOptions ++= Seq(
      "-no-indent"
    )
  )
