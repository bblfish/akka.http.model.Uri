package akka.http.test

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import org.scalajs.dom.{Node, document}

import scala.scalajs.js


/**
 * Created by hjs on 05/08/2014.
 */
object ScalaReactSpeedTest extends js.JSApp {
  @js.annotation.JSExport
  override def main() = {
    example1(document getElementById "eg1")
  }

  def example1(mountNode: Node) = {

    val HelloMessage = ReactComponentB[String]("HelloMessage")
      .render(name => div("Hello ", name))
      .create

    React.renderComponent(HelloMessage("John"), mountNode)
  }

}
