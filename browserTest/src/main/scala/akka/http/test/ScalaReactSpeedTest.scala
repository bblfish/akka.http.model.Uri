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
    example2(document getElementById "eg1")
  }

  def example(mountNode: Node) = {

    val HelloMessage = ReactComponentB[String]("HelloMessage")
      .render(name => div("Hello ", name))
      .create

    React.renderComponent(HelloMessage("John"), mountNode)
  }

  def example2(mountNode: Node) = {
    case class State(pointer: Int, pageSize:Int)

    class Backend(t: BackendScope[IndexedSeq[Int], State]) {
      def prevPage() = t.modState(s => s.copy(pointer =
        Math.max(0,s.pointer-s.pageSize)
      ))
      def nextPage() = t.modState(s => s.copy(pointer =
        Math.min(s.pointer + s.pageSize, Math.max(0,t.props.size-s.pageSize))))
    }

    val TableView = ReactComponentB[IndexedSeq[Int]]("TableView")
       .initialState(State(0,10))
       .backend(new Backend(_))
       .render{ (seq, S, B) =>
         def row(i: Int) = tr(td(""+i))
      div(
         table(tbody(for (r <- seq.slice(S.pointer,S.pointer+S.pageSize)) yield row(r))),
         button(onclick-->B.prevPage())("previous"),button(onclick-->B.nextPage())("next")
      )
    }.create

    val s = for (i <- 1 to 100) yield i
    React.renderComponent(TableView(s), mountNode)


  }

}
