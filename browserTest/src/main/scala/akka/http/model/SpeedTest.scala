package akka.http.model

import java.util.Date

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.jquery.jQuery

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success, Try}

object SpeedTest  extends JSApp {

  def main() {
    appendPar(document.body, "Hello World")
  }

  @JSExport
  def analyse(urisText: String) {
    println("hello")
    val start = new Date()
    val processed = for ( uriStr <- urisText.split("\n")) yield {
        (uriStr, Try{ Uri(uriStr.trim) })

    }
    val end = new Date()
    jQuery("#report").text(s"It took ${end.getTime-start.getTime} milliseconds to process ${processed.length} uris")
    val tbody = jQuery("tbody")
    val trow = jQuery("#UriDisplay tbody tr:first").clone()
    for (item<-processed) {
      val (uriStr,parsing) = item
      val row = trow.clone()
      row.find(".uriStr").text(uriStr)
      parsing match {
        case Success(uri) => {
          import uri._
          row.find(".answer").text(s"$scheme || ${authority.toString} || ${path.toString} || ${fragment.toString} ")
        }
        case Failure(e) => {
          row.find(".answer").attr("class","err").text(e.getMessage)
        }
      }
      tbody.append(row)
    }
    //    val textNode = document.createTextNode()
    //    targetNode.appendChild(parNode)
    println("end analyse")
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    println("appendPar")
  }
}
