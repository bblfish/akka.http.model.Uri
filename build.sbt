scalaJSSettings

name := "Port of akka.http Uri class to ScalaJS"

scalaVersion := "2.11.1"

resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/content/alexander-myltsev/maven"

libraryDependencies += "name.myltsev" %%% "parboiled" % "2.0.0"

libraryDependencies +=  "org.scala-lang"  %  "scala-reflect"      % "2.11.1"   % "provided"

