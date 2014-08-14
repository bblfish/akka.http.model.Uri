package net.bblfish.test

import java.util.Date

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import org.scalajs.dom.{Node, document}
import shapeless._
import shapeless.syntax.std.tuple._

import scala.math.Ordering
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
    val now = System.currentTimeMillis()
    val chars = ('A' to 'z').reverse
    val s = for (n <- 1 to 300) yield {
      (n, chars(n%chars.length), new Date(now-n*1000)).productElements
    }
    val h = ("num","char","date").productElements

    React.renderComponent(TableView(Table(h,s)).create, mountNode)
  }

  case class TableView[H <: HList, R <: HList](tableData: Table[H,R])
                                              (implicit extractr: hlistaux.Extractor[_0, R,R]) {


    case class State(pointer: Int, pageSize: Int, sortedRows: Seq[R])


    class Backend(t: BackendScope[Table[H, R], State]) {
      def sort[O](sort: hlistaux.Sorter[R]): Unit =
        t.modState { s =>
          val sorted = sort(tableData.rows)
          val pointed = s.sortedRows(s.pointer)
          val i = sorted.indexOf(pointed)
          s.copy(sortedRows = sorted,pointer=i)
        }


      def prevPage() = t.modState(s => s.copy(pointer =
        Math.max(0, s.pointer - s.pageSize)
      ))

      def nextPage() = t.modState(s => s.copy(pointer =
        Math.min(s.pointer + s.pageSize, Math.max(0, t.props.rows.size - s.pageSize))))
    }

    def tableView = ReactComponentB[Table[H,R]]("TableView")
      .initialState(State(0, 10,tableData.rows))
      .backend(new Backend(_))
      .render { (tab: Table[H,R], S: State, B: Backend) =>
      def row(r: R) = tr(for (e <- r.toList) yield td(s"$e"))
      val seq = S.sortedRows

      def header = {
//        val hdrsAndFuncs = tableData.hdrs.zip(e)
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


}

import shapeless._
import shapeless.ops.hlist.At
import shapeless.syntax.std.tuple._

final class myHListOps[L <: HList](l: L) {

  import net.bblfish.test.hlistaux._

  def extractors(implicit extractor : Extractor[_0, L,L]) : extractor.Out = extractor()
}

object hlistaux {
  type Sorter[R] = Seq[R] => Seq[R]

  trait Extractor[HF<:Nat, In <: HList, Remaining<: HList] extends DepFn0 { type Out <: List[Sorter[In]] }

  def sort[R<:HList,O,N<:Nat](att: At.Aux[R,N,O], ord: Ordering[O]): Sorter[R] = (hlists: Seq[R]) => hlists.sortBy(att(_))(ord)
  object Extractor {
    def apply[HL <: HList]
    (implicit extractor: Extractor[_0, HL,HL]):
       Aux[_0, HL, HL, extractor.Out] = extractor

    type Aux[HF<:Nat, In <: HList, Remaining<: HList, Out0 <: List[Sorter[In]]] =
        Extractor[HF, In, Remaining] { type Out = Out0 }

    //To deal with case where HNil is passed. not sure if this is right.
    implicit def hnilExtractor: Aux[_0, HNil, HNil, List[Nothing]] =
      new Extractor[_0, HNil, HNil] {
        type Out = List[Nothing]
        def apply(): Out = Nil
      }

    implicit def hSingleExtractor1[N<:Nat, In<:HList, H ]
    (implicit att : At.Aux[In, N,H], ordering: Ordering[H]): Aux[N, In, H::HNil, List[Sorter[In]]] =
      new Extractor[N, In, H::HNil] {
        type Out = List[Sorter[In]]
        def apply(): Out = List(sort(att,ordering))
      }


    implicit def hlistExtractor1[N <: Nat, In<:HList, H, Tail<: HList]
    (implicit mt : Extractor[Succ[N], In, Tail],
        ordering: Ordering[H],
              att : At.Aux[In, N,H])
    :Aux[N, In, H::Tail, List[Sorter[In]]] = {
      new Extractor[N, In, H::Tail] {
        type Out = List[Sorter[In]]
        def apply(): Out = sort(att,ordering)::mt()
      }
    }
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



