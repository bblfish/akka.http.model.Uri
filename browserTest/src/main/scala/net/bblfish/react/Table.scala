package net.bblfish.react

import shapeless._

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