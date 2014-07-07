scalaJSSettings

name := "akka.http.model.Uri.scalajs"

scalaVersion := "2.11.1"

resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/content/alexander-myltsev/maven"

libraryDependencies += "name.myltsev" %%% "parboiled" % "2.0.0"

libraryDependencies += "com.lihaoyi" %%% "utest" % "0.1.7" % "test"

// to be removed as soon as parboiled issue is fixed
// https://github.com/sirthias/parboiled2/issues/81

libraryDependencies +=  "org.scala-lang"  %  "scala-reflect"      % "2.11.1"   % "provided"

// only needed for speed test - should be moved to a different subproject

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"

utest.jsrunner.Plugin.utestJsSettings
