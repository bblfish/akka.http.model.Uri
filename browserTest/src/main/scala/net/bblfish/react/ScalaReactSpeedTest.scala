package net.bblfish.react

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import scala.scalajs.js
import shapeless.syntax.std.tuple._


import org.scalajs.dom.{Node, document}

/**
 * Created by hjs on 05/08/2014.
 */
object ScalaReactSpeedTest extends js.JSApp {
  @js.annotation.JSExport
  override def main() = {
    example2(document getElementById "eg1")
  }

  def example(mountNode: Node) = {

    val HelloMessage = ReactComponentB[String]("HelloMessage")
      .render(name => div("Hello ", name))
      .create

    React.renderComponent(HelloMessage("John"), mountNode)
  }

  /**
   *  Trying to create a generic table viewer, that can show views of a given
    * data structure, and later sort columns by clicking on the header.
    * @param mountNode the html node to drop the table into
    * @return
    */
  def example2(mountNode: Node) = {
    import java.util.Date
    val now = System.currentTimeMillis()
    val chars = ('A' to 'z').reverse
    val s = for (n <- 1 to 300) yield {
      (n, chars(n%chars.length), new Date(now-n*1000)).productElements
    }
    val h = ("num","char","date").productElements

    React.renderComponent(TableView(Table(h,s)).create, mountNode)
  }



}



