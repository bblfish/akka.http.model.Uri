package net.bblfish.react

import shapeless._
import shapeless.ops.hlist.At

import scala.math.Ordering

/**
 * Created by hjs on 14/08/2014.
 */
object HListSort {
  type Sorter[R] = Seq[R] => Seq[R]

  trait HListSeqSorter[HF<:Nat, In <: HList, Remaining<: HList] extends DepFn0 { type Out <: List[Sorter[In]] }

  def sort[R<:HList,O,N<:Nat](att: At.Aux[R,N,O], ord: Ordering[O]): Sorter[R] = (hlists: Seq[R]) => hlists.sortBy(att(_))(ord)
 
  object HListSeqSorter {
    def apply[HL <: HList]
    (implicit extractor: HListSeqSorter[_0, HL,HL]):
       Aux[_0, HL, HL, extractor.Out] = extractor

    type Aux[HF<:Nat, In <: HList, Remaining<: HList, Out0 <: List[Sorter[In]]] =
        HListSeqSorter[HF, In, Remaining] { type Out = Out0 }

    //To deal with case where HNil is passed. not sure if this is right.
    implicit def hnilExtractor: Aux[_0, HNil, HNil, List[Nothing]] =
      new HListSeqSorter[_0, HNil, HNil] {
        type Out = List[Nothing]
        def apply(): Out = Nil
      }

    implicit def hSingleExtractor1[N<:Nat, In<:HList, H ]
    (implicit att : At.Aux[In, N,H], ordering: Ordering[H]): Aux[N, In, H::HNil, List[Sorter[In]]] =
      new HListSeqSorter[N, In, H::HNil] {
        type Out = List[Sorter[In]]
        def apply(): Out = List(sort(att,ordering))
      }


    implicit def hlistExtractor1[N <: Nat, In<:HList, H, Tail<: HList]
    (implicit mt : HListSeqSorter[Succ[N], In, Tail],
        ordering: Ordering[H],
              att : At.Aux[In, N,H])
    :Aux[N, In, H::Tail, List[Sorter[In]]] = {
      new HListSeqSorter[N, In, H::Tail] {
        type Out = List[Sorter[In]]
        def apply(): Out = sort(att,ordering)::mt()
      }
    }
  }
}
