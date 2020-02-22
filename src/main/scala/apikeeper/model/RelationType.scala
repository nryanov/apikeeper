package apikeeper.model

import enumeratum._

import scala.collection.immutable

sealed trait RelationType extends EnumEntry

object RelationType extends Enum[RelationType] with CirceEnum[RelationType] {
  override def values: immutable.IndexedSeq[RelationType] = findValues

  case object In extends RelationType

  case object Out extends RelationType
}
