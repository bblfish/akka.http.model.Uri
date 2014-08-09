package net.bblfish.test

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

  /**
   *  Trying to create a generic table viewer, that can show views of a given
    * data structure, and later sort columns by clicking on the header.
    * @param mountNode the html node to drop the table into
    * @return
    */
  def example2(mountNode: Node) = {
    val s = for (i <- (1 to 300 by 3)) yield Tuple3(i,i+1,i+2).productElements
    val h = Tuple3("one","two","three").productElements

    React.renderComponent(TableView(Table(h,s)).create, mountNode)
  }

  case class TableView[H <: HList, R <: HList](tableData: Table[H,R]) {

    case class State(pointer: Int, pageSize: Int, sortedRows: Option[Seq[R]] = None)

    class Backend(t: BackendScope[Table[H, R], State]) {
      def sort[Elem<:Nat](lens: Lens[R, Elem]) = t.modState(_.copy(sortedRows=
        Some(tableData.rows.sortBy(lens.get(_)))
      ))

      def prevPage() = t.modState(s => s.copy(pointer =
        Math.max(0, s.pointer - s.pageSize)
      ))

      def nextPage() = t.modState(s => s.copy(pointer =
        Math.min(s.pointer + s.pageSize, Math.max(0, t.props.rows.size - s.pageSize))))
    }

    def tableView = ReactComponentB[Table[H,R]]("TableView")
      .initialState(State(0, 10))
      .backend(new Backend(_))
      .render { (tab: Table[H,R], S: State, B: Backend) =>
      def row(r: R) = tr(for (e <- r.toList) yield td(s"$e"))
      val seq = tab.rows

      def header = {
        tr(for (ci <- 0 to tab.hdrs.runtimeLength) yield th(onclick --> B.sort(hlistNthLens[R,nat(ci)]))(tab.hdrs(ci).toString))
      }
      div(
        table(
          thead(header),
          tbody(for (r <- seq.slice(S.pointer, S.pointer + S.pageSize)) yield row(r))
        ),
        button(onclick --> B.prevPage())("previous"), button(onclick --> B.nextPage())("next")
      )
    }


    def create = tableView.create(tableData)
  }


}

/**
 * Code taken from https://gist.github.com/milessabin/6814566
 */
class Table[TH<:HList, TR<:HList](val hdrs: TH, val rows: Seq[TR])

object Table {
    def apply[TH<:HList, TR<:HList](hdrs: TH, rows: Seq[TR])
                     (implicit ts: TableShape[TH, TR]) = new Table(hdrs, rows)

    trait TableShape[TH, TR]

    object TableShape {
      implicit def productTableShape[TH, TR, LH, LR]
      (implicit
//       genH: Generic.Aux[TH, LH],
//       genR: Generic.Aux[TR, LR],
       hlistShape: TableShape[LH, LR]): TableShape[TH, TR] = new TableShape[TH, TR] {}

      implicit def hsingleTableShape[RH]: TableShape[String :: HNil, RH :: HNil] =
        new TableShape[String :: HNil, RH :: HNil] {}

      implicit def hlistTableShape[HT <: HList, RH, RT <: HList]
      (implicit tailShape: TableShape[HT, RT]): TableShape[String :: HT, RH :: RT] =
        new TableShape[String :: HT, RH :: RT] {}
    }

  }



