package test

import java.util.Date

import node.scalajs.Url._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.util.Try

/**
 * Created by hjs on 07/07/2014.
 */
object cliJSTest extends JSApp {

  override
  def main(): Unit = {
    val fs = js.Dynamic.global.require("fs")
    val file = "rl_links.txt"
    val content: String = fs.readFileSync(file,js.Dynamic.literal(encoding="utf8")).asInstanceOf[String]
    val lines = content.split("\n")
    println("lines.length="+lines.length)
    val start = new Date()
    val processed = for (line <- lines) yield {
      (line, Try{Url.parse(line.trim())})
    }
    val end = new Date()
    println(s"It took ${end.getTime-start.getTime} milliseconds to process ${processed.length} uris")
  }


}
