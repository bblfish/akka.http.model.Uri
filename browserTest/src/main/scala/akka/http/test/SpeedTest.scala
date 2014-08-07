//package akka.http.test
//
//import java.net.{URI => jURI}
//import java.util.Date
//
//import akka.http.model.{Uri => AkkaUri, _}
//import node.scalajs.Url.{Url => NodeURL}
//import org.scalajs.dom
//import org.scalajs.dom.document
//import org.scalajs.jquery.jQuery
//
//import scala.scalajs.js.JSApp
//import scala.scalajs.js.annotation.JSExport
//import scala.util.Try
//import scala.util.control.NonFatal
//
//object SpeedTest  extends JSApp {
//
//  case class Result[U](parser: String, result: Array[(String,Try[U])], time: Long)
//
//  def main() {
//    appendPar(document.body, "Hello World")
//  }
//
//  var trimmed: String = _
//
//  @JSExport
//  def parsingDiffs(urisText: String) {
//    val lines = urisText.split("\n")
//    def equ(a: Try[bURI], b: Try[bURI]): Boolean = {
//      // we can't tell if the failures are equal
//      a.isSuccess && b.isSuccess && a.get.equalsUri(b.get)
//    }
//    var line = 0
//    val tbody = jQuery("tbody")
//    val trow = jQuery("#UriDisplay tbody tr:first").clone()
//
//    for ( uriStr <- lines) yield {
//      trimmed = uriStr.trim
//      line += 1
//      val akka = Try{ new bAkkaURI(AkkaUri(trimmed)) }
//      val java = Try{ new bjavaURI(new jURI(trimmed)) }
//      val node = Try{ new bNodeURI( NodeURL.parse(trimmed)) }
//      val row = trow.clone()
//      def form(t: String, u: Try[bURI]) {
//        try {
//          row.find("." + t).text("" + u)
//        } catch {
//          case NonFatal(e) => row.find("." + t).text("OOPS: trying to write result caused: \n"+e.toString)
//        }
//      }
//      if ( !equ(akka,java) || !equ(java,node) || !equ(node,akka) ) {
//        row.find(".line").text(""+line)
//        row.find(".uriStr").text(uriStr)
//        form("akka",akka)
//        form("java",java)
//        form("node",node)
//        tbody.append(row)
//      }
//    }
//
//  }
//
//  @JSExport
//  def analyse(urisText: String) {
//    println("analyse lib=" + jQuery("#lib").value())
//    val result = jQuery("#lib").value().toString match {
//      case "akka" => analyseAkka(urisText)
//      case "java" => analyseJava(urisText)
//      case "node" => analyseNode(urisText)
//    }
//    report(result)
//  }
//
//  def toUris[U](urisText: String, parse: String => U): Array[(String,Try[U])] = {
//    val lines = urisText.split("\n")
//    for ( uriStr <- lines) yield {
//      (uriStr, Try{ parse(uriStr.trim) })
//    }
//  }
//
//  def analyseJava(urisText: String) = {
//    println("analysing java")
//    val start = new Date()
//    val processed = toUris(urisText,s=>new jURI(s))
//    val end = new Date()
//    Result("java",processed,end.getTime-start.getTime)
////    report(processed,end.getTime-start.getTime)
//    //   val textNode = document.createTextNode()
//    //   targetNode.appendChild(parNode)
//    //   println("end analysis")
//  }
//
//  def analyseAkka(urisText: String) = {
//    println("analysing akka")
//    val start = new Date()
//    val processed = toUris(urisText,AkkaUri.apply)
//    val end = new Date()
//    Result("akka",processed,end.getTime-start.getTime)
//    //    report(processed,end.getTime-start.getTime)
//    //    val textNode = document.createTextNode()
//    //    targetNode.appendChild(parNode)
//    //    println("end analysis")
//  }
//
//  def report[U](result: Result[U]) {
//    val groups = result.result.groupBy{
//      case (uri,trie) => trie.isSuccess
//    }.withDefaultValue(Array())
//    jQuery("#report").text(
//      s"""
//         |It took ${result.time} milliseconds to process ${result.result.length} uris.
//         |Of these there were ${groups(true).size} successes and ${groups(false).size} failures.
//         |""".stripMargin)
////    val tbody = jQuery("tbody")
////    val trow = jQuery("#UriDisplay tbody tr:first").clone()
////    for (item<-processed) {
////      val (uriStr,parsing) = item
////      val row = trow.clone()
////      row.find(".uriStr").text(uriStr)
////      parsing match {
////        case Success(uri) => {
////          import uri._
////          row.find(".answer").text(s"$scheme || ${authority.toString} || ${path.toString} || ${fragment.toString} ")
////        }
////        case Failure(e) => {
////          row.find(".answer").attr("class","err").text(e.getMessage)
////        }
////      }
////      tbody.append(row)
////    }
//  }
//
//  @JSExport
//  def analyseNode(urisText: String) = {
//    println("analysing node")
//    val start = new Date()
//    val processed = toUris(urisText,NodeURL.parse(_))
//    val end = new Date()
//    Result("java",processed,end.getTime-start.getTime)
////    report(processed,end.getTime-start.getTime)
//
////    val end = new Date()
////    jQuery("#report").text(s"It took ${end.getTime-start.getTime} milliseconds to process ${processed.length} uris")
////    val tbody = jQuery("tbody")
////    val trow = jQuery("#UriDisplay tbody tr:first").clone()
////    for ((uriStr,parsing)<-processed.slice(0,100)) {
////      val row = trow.clone()
////      row.find(".uriStr").text(uriStr)
////      parsing match {
////        case Success(uri) => {
////          import uri._
////          row.find(".answer").text(s"$protocol || ${host} || ${path} || ${search} || ${hash}")
////        }
////        case Failure(e) => {
////          row.find(".answer").attr("class","err").text(e.getMessage)
////        }
////      }
////      tbody.append(row)
////    }
//    //    val textNode = document.createTextNode()
//    //    targetNode.appendChild(parNode)
//  }
//
//
//  def appendPar(targetNode: dom.Node, text: String): Unit = {
//    println("appendPar")
//  }
//}
