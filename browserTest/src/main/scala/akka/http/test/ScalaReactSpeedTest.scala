package akka.http.test

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import org.scalajs.dom.{Node, document}
import shapeless._
import shapeless.syntax.std.tuple._

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
    val s = for (i <- (1 to 100 by 2)) yield Tuple2(i,i+1).productElements
    val h = ("even","odd").productElements

    React.renderComponent(TableView(h,s).create, mountNode)
  }

  case class TableView[H <: HList, R <: HList](h: H, s: Seq[R]) {

    case class State(pointer: Int, pageSize: Int)

    class Backend(t: BackendScope[Table[H, R], State]) {
      def prevPage() = t.modState(s => s.copy(pointer =
        Math.max(0, s.pointer - s.pageSize)
      ))

      def nextPage() = t.modState(s => s.copy(pointer =
        Math.min(s.pointer + s.pageSize, Math.max(0, t.props.rows.size - s.pageSize))))
    }

    def tv = ReactComponentB[Table[H, R]]("TableView")
      .initialState(State(0, 10))
      .backend(new Backend(_))
      .render(rendr _)

    def rendr(tab: Table[H, R], S: State, B: Backend): japgolly.scalajs.react.vdom.ReactOutput = {
      def row(r: R) = tr(for (e <- r.toList) yield td(s"$e"))
      val seq = tab.rows

      div(
        table(
          thead(tr(for (h <- tab.hdrs.toList) yield th(h.toString))),
          tbody(for (r <- seq.slice(S.pointer, S.pointer + S.pageSize)) yield row(r))
        ),
        button(onclick --> B.prevPage())("previous"), button(onclick --> B.nextPage())("next")
      )
    }

    def t = Table[H,R](h, s)

    def create = tv.create(t)
  }


}

class Table[TH, TR](val hdrs: TH, val rows: Seq[TR])

object Table {
    def apply[TH, TR](hdrs: TH, rows: Seq[TR])
                     (implicit ts: TableShape[TH, TR]) = new Table(hdrs, rows)

    trait TableShape[TH, TR]

    object TableShape {
      implicit def productTableShape[TH, TR, LH, LR]
      (implicit
       genH: Generic.Aux[TH, LH],
       genR: Generic.Aux[TR, LR],
       hlistShape: TableShape[LH, LR]): TableShape[TH, TR] = new TableShape[TH, TR] {}

      implicit def hsingleTableShape[RH]: TableShape[String :: HNil, RH :: HNil] =
        new TableShape[String :: HNil, RH :: HNil] {}

      implicit def hlistTableShape[HT <: HList, RH, RT <: HList]
      (implicit tailShape: TableShape[HT, RT]): TableShape[String :: HT, RH :: RT] =
        new TableShape[String :: HT, RH :: RT] {}
    }

  }



