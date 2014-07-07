akka.http.model.Uri
===================

port of akka/spray's [Uri class](http://doc.akka.io/api/akka-stream-and-http-experimental/0.4/#akka.http.model.Uri) to [Scala.js](http://www.scala-js.org/). The original code is to be found on the [relase-2.3-dev](https://github.com/akka/akka/blob/release-2.3-dev/akka-http-core/src/main/scala/akka/http/model/Uri.scala) branch of akka.

This project is made up of a few subprojects:
 * [http.model.Uri](Uri) : the project that adapts the akka code to scalaJS
 * [browserTest](browserTest): a project to test loading a file with one URL per line into a browser. Useful for evaluating the speed of parsing in different browsers. 
 * cliTest: A project to test the akka original libraries parsing speed
