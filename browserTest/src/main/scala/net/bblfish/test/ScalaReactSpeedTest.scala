package net.bblfish.test

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactVDom._
import japgolly.scalajs.react.vdom.ReactVDom.all._
import org.scalajs.dom.{Node, document}
import shapeless._
import shapeless.ops.hlist.At
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
    val s = for (i <- (1 to 300 by 3)) yield Tuple3(i,i+1,i+2).productElements
    val h = Tuple3("one","two","three").productElements

    React.renderComponent(TableView(Table(h,s)).create, mountNode)
  }

  case class TableView[H <: HList, R <: HList](tableData: Table[H,R]) {

    case class State(pointer: Int, pageSize: Int, sortedRows: Option[Seq[R]] = None)


    class Backend(t: BackendScope[Table[H, R], State]) {
      def sort[Elem<:Nat](column: At[R, Elem])(implicit ord: Ordering[column.Out]) = t.modState(s=>
        s.copy(sortedRows =
          Some(tableData.rows.sortBy(r => column(r)))
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
        import hlistaux._
        val e = new myHListOps(tableData.rows.head).extractors
        val hdrsAndFuncs = e.zip(tableData.hdrs)
        import poly._

        // The same definition of choose as above
        object trans extends ((Function1,String) ~> TypedTag) {
          def apply[T](st : (Function1,String)) = th(onclick --> B.sort(p=>st._1))(tab.hdrs(ci).toString))
        }
        hdrsAndFuncs map trans
        //tr(for (ci <- 0 to tab.hdrs.runtimeLength) yield th(onclick --> B.sort(p=>p.at()))(tab.hdrs(ci).toString))
      }
      div(
        table(
          thead(th("h"),header),
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

final class myHListOps[H,L <: HList](l : H::L) {

  import hlistaux._

  def extractors(implicit extractor : Extractor[_0, H::L,H::L]) : extractor.Out = extractor()
}

object hlistaux {
  trait Extractor[HF<:Nat, In <: HList, Remaining<: HList] extends DepFn0 { type Out <: HList }

  object Extractor {
    def apply[HL <: HList]
    (implicit extractor: Extractor[_0, HL,HL]):
       Aux[_0, HL, HL, extractor.Out] = extractor

    type Aux[HF<:Nat, In <: HList, Remaining<: HList, Out0 <: HList] = Extractor[HF, In, Remaining] { type Out = Out0 }

    implicit def hnilExtractor1[N<:Nat, In<:HList, H ]
    (implicit att : At[In, N]): Aux[N, In, H::HNil, Function1[In,att.Out]::HNil] =
      new Extractor[N, In, H::HNil] {
        type Out = Function1[In,att.Out]::HNil
        val f : Function1[In,att.Out] = (i: In) => att.apply(i)
        def apply(): Out = f::HNil
      }


    implicit def hlistExtractor1[N <: Nat, In<:HList, H, Tail<: HList]
    (implicit mt : Extractor[Succ[N], In, Tail],
              att : At[In, N])
    :Aux[N, In, H::Tail, Function1[In,att.Out]::mt.Out] = {
      new Extractor[N, In, H::Tail] {
        type Out = Function1[In,att.Out]::mt.Out

        def apply(): Out = {
          val f : Function1[In,att.Out] = (i: In) => att.apply(i)
          f :: mt()
        }
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



