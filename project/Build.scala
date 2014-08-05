import sbt.Keys._
import sbt._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

object ScalajsReact extends Build {

  type PE = Project => Project

  lazy val commonSettings: PE = _.settings(
    organization := "net.bblfish",
    scalaVersion := "2.11.2",
    version := "0.1",
    description := "Akka's Uri ported to Scala",
    licenses := Seq("Apache License, Version 2.0" -> url("http://opensource.org/licenses/Apache-2.0")),
    homepage := Some(url("https://github.com/bblfish/akka.http.model.Uri")),
    publishTo := {
      //eg: export SBT_PROPS=-Dbanana.publish=bblfish.net:/home/hjs/htdocs/work/repo/
      val nexus = "https://oss.sonatype.org/"
      val other = Option(System.getProperty("banana.publish")).map(_.split(":"))
      if (version.value.trim.endsWith("SNAPSHOT")) {
        val repo = other.map(p => Resolver.ssh("banana.publish specified server", p(0), p(1) + "snapshots"))
        repo.orElse(Some("snapshots" at nexus + "content/repositories/snapshots"))
      } else {
        val repo = other.map(p => Resolver.ssh("banana.publish specified server", p(0), p(1) + "releases"))
        repo.orElse(Some("releases" at nexus + "service/local/staging/deploy/maven2"))
      }
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false}
  )

  //suggested that I add relativeSourceMaps see https://github.com/japgolly/scalajs-react/issues/14
  lazy val scalaJSSettingsPlus = scalaJSSettings ++ Seq(
    relativeSourceMaps := true
  )
  // only needed for speed test - should be moved to a different subproject

  utest.jsrunner.Plugin.utestJsSettings

  lazy val root = project.in(file(".")).aggregate()

  lazy val UriJS = project.in(file("akka.UriJS"))
    .configure(commonSettings)
    .settings(scalaJSSettingsPlus:_*)
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
    .configure(commonSettings)
    .settings(scalaJSSettingsPlus:_*)
    .settings(
      name := "node.Uri",
      ScalaJSKeys.jsDependencies += ProvidedJS / "node.uri.bundle.js"
    )

  def useReact(scope: String = "compile"): PE =
    _.settings(
      jsDependencies += "org.webjars" % "react" % "0.11.1" % scope / "react-with-addons.js" commonJSName "React",
      skip in packageJSDependencies := false)


  lazy val browserTest = project.in(file("browserTest"))
    .configure(commonSettings)
    .settings(scalaJSSettingsPlus:_*)
    .dependsOn(UriJS)
    .settings(
      name := "http.model.Uri.browsertest",
      skip in packageJSDependencies := false,
      libraryDependencies ++= Seq(
        "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
        "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
        "net.bblfish" %%% "node-scalajs" % "0.1",
        //scalaz-react-js ( as I like to call it )
        "com.scalatags" %%% "scalatags" % "0.3.5",
        "com.github.japgolly.scalajs-react" %%% "core" % "0.4.0",
        "com.github.japgolly.scalajs-react" %%% "test" % "0.4.0" % "test",
        "com.github.japgolly.scalajs-react" %%% "ext-scalaz71" % "0.4.0"
      )
    )

  lazy val cliTest = project.in(file("cliTest"))
    .configure(commonSettings)
    .settings(
      name := "Uri.cliTest",
      libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % "0.4"
    )

  lazy val cliJSTest = project.in(file("cliJSTest"))
    .configure(commonSettings)
    .settings(scalaJSSettingsPlus:_*)
    .dependsOn(NodeUriJS)
    .settings(
      name := "Uri.cliJSTest",
      //    ScalaJSKeys.jsDependencies += ProvidedJS / "node..bundle.js",
      skip in ScalaJSKeys.packageJSDependencies := false
    )
}