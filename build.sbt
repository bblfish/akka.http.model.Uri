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

lazy val UriJS = project.in(file("akka.UriJS"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    name := "akka.UriJS",
    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/content/alexander-myltsev/maven",
    libraryDependencies += "name.myltsev" %%% "parboiled" % "2.0.0",
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.1.7" % "test",
    // to be removed as soon as parboiled issue is fixed
    // https://github.com/sirthias/parboiled2/issues/81
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.1" % "provided"
  )

lazy val NodeUriJS = project.in(file("node.Uri"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    name := "node.Uri",
    ScalaJSKeys.jsDependencies += ProvidedJS / "node.uri.bundle.js"
  )


lazy val browserTest = project.in(file("browserTest"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .dependsOn(UriJS)
  .settings(
  name := "http.model.Uri.browsertest",
  libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
  libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
  libraryDependencies += "net.bblfish" %%% "node-scalajs" % "0.1"
)

lazy val cliTest = project.in(file("cliTest"))
  .settings(commonSettings: _*)
  .settings(
    name := "Uri.cliTest",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % "0.4"
  )

lazy val cliJSTest = project.in(file("cliJSTest"))
  .settings(scalaJSSettings: _*)
  .settings(commonSettings: _*)
  .dependsOn(NodeUriJS)
  .settings(
    name := "Uri.cliJSTest",
//    ScalaJSKeys.jsDependencies += ProvidedJS / "node..bundle.js",
    skip in ScalaJSKeys.packageJSDependencies := false
  )
