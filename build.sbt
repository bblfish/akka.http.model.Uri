import sbt.Keys._

scalaJSSettings

lazy val commonSettings = Seq(
    organization := "net.bblfish",
    scalaVersion := "2.11.1",
    version := "0.1"
)

// only needed for speed test - should be moved to a different subproject

utest.jsrunner.Plugin.utestJsSettings

lazy val root = project.in(file(".")).aggregate()

lazy val UriJS = project.in(file("Uri"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    name := "http.model.Uri",
    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/content/alexander-myltsev/maven",
    libraryDependencies += "name.myltsev" %%% "parboiled" % "2.0.0",
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.1.7" % "test",
    // to be removed as soon as parboiled issue is fixed
    // https://github.com/sirthias/parboiled2/issues/81
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.1" % "provided"
  )

lazy val browserTest = project.in(file("browserTest"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .dependsOn(UriJS)
  .settings(
  name := "http.model.Uri.browsertest",
  libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
  libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"
)