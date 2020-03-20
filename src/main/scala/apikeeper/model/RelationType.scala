package apikeeper.model

import enumeratum._

import scala.collection.immutable

sealed trait RelationType extends EnumEntry

object RelationType extends Enum[RelationType] with CirceEnum[RelationType] {
  override def values: immutable.IndexedSeq[RelationType] = findValues

  case object Upstream extends RelationType

  case object Downstream extends RelationType
}
