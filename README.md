akka.http.model.Uri
===================

port of akka/spray's [Uri class](http://doc.akka.io/api/akka-stream-and-http-experimental/0.4/#akka.http.model.Uri) to [Scala.js](http://www.scala-js.org/). The original code is to be found on the [relase-2.3-dev](https://github.com/akka/akka/blob/release-2.3-dev/akka-http-core/src/main/scala/akka/http/model/Uri.scala) branch of akka.

This project is made up of a few subprojects:
 * [http.model.Uri](Uri) : the project that adapts the akka code to scalaJS
 * [browserTest](browserTest): a project to test loading a file with one URL per line into a browser. Useful for evaluating the speed of parsing in different browsers. 
 * cliTest: A project to test the akka original libraries parsing speed

To test the speed of the parsing you can try the file [links.zip](https://code.google.com/p/whalebot/downloads/detail?name=links.zip) which unzipped is 9.1MB and contains 226783 URLs . On my MacBookPro Retina this takes 37 seconds to parse in Chrome,  1.453 seconds to parse in the pure scala command line, and 4.226 seconds using the [node.js Uri library](https://github.com/bblfish/node.scalajs). This suggests that parsing and canonicalisation of URLs should be done on the server, which should as much as possible send zipped and canonicalised [n-triples](http://www.w3.org/TR/n-triples/) files to the client. The client should then do as little as possible URI analysis - perhaps not more than removing #fragments. The client should stick as much as possible to simple string comparison.

In order to run the browser test suite you need to first build [node.scalajs](https://github.com/bblfish/node.scalajs) and make sure the [browserTest/speedTest.html](browserTest/speedTest.html) is pointing to the created bundle.js file which you create. I am looking for ways of automating that whole build. ( please contact me if you know how).
