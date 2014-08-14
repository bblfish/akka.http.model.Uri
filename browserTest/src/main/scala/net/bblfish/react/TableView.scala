package net.bblfish.react


import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import shapeless._
import shapeless.syntax.std.tuple._

/**
 * Created by hjs on 14/08/2014.
 */
case class TableView[H <: HList, R <: HList](tableData: Table[H,R], rowsPerPage: Int=40)
                                            (implicit extractr: HListSort.HListSeqSorter[_0, R,R]) {


  case class State(pointer: Int, pageSize: Int, sortedRows: Seq[R])


  class Backend(t: BackendScope[Table[H, R], State]) {
    def sort[O](sort: HListSort.Sorter[R]): Unit =
      t.modState { s =>
        val sorted = sort(tableData.rows)
        val pointed = s.sortedRows(s.pointer)
        val i = sorted.indexWhere(_ eq pointed) // find object ref equality
        s.copy(sortedRows = sorted,pointer=i)
      }

    def prevPage() = t.modState(s => s.copy(pointer =
      Math.max(0, s.pointer - s.pageSize)
    ))

    def nextPage() = t.modState(s => s.copy(pointer =
      Math.min(s.pointer + s.pageSize, Math.max(0, t.props.rows.size - s.pageSize))))
  }

  def tableView = ReactComponentB[Table[H,R]]("TableView")
    .initialState(State(0, pageSize = rowsPerPage,tableData.rows))
    .backend(new Backend(_))
    .render { (tab: Table[H,R], S: State, B: Backend) =>
    def row(r: R) = tr(for (e <- r.toList) yield td(s"$e"))
    val seq = S.sortedRows

    def header = {
      //        val hdrsAndFuncs = tableData.hdrs.zip(e)
      // can't use zip for some reason, so need to use this horrible injection of an iterable
      val it = tableData.hdrs.toList.toIterator
      for (e <- extractr())
      yield  th(onclick --> B.sort(e))(label(it.next().toString))
    }

    div(
      table(
        thead(tr(header)),
        tbody(for (r <- seq.slice(S.pointer, S.pointer + S.pageSize)) yield row(r))
      ),
      button(onclick --> B.prevPage())("previous"), button(onclick --> B.nextPage())("next")
    )
  }

  def create = tableView.create(tableData)
}
