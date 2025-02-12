val scala3Version = "3.6.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "large letter boxes",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )
