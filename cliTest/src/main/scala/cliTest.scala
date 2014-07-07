package test

import java.io.File
import java.util.Date

import akka.http.model.Uri

import scala.io.{Codec, Source}
import scala.util.Try

/**
 * Created by hjs on 07/07/2014.
 */
object cliTest {

  def main(args: Array[String]) {
    val file = new File(args(0))
    val source = Source.fromFile(file)(Codec.ISO8859)
    val lines = source.getLines().toList
    source.close()
    println("lines.length="+lines.length)
    val start = new Date()
    val processed = for (line <- lines) yield {
      (line, Try{Uri.apply(line.trim())})
    }
    val end = new Date()
    println(s"It took ${end.getTime-start.getTime} milliseconds to process ${processed.length} uris")
  }

}
