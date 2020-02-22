package apikeeper.model

import enumeratum._

import scala.collection.immutable

sealed trait EntityType extends EnumEntry

object EntityType extends Enum[EntityType] with CirceEnum[EntityType] {
  override def values: immutable.IndexedSeq[EntityType] = findValues

  case object Service extends EntityType

  case object Storage extends EntityType

  case object MessageQueue extends EntityType
}
