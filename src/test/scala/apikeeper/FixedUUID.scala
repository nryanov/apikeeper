package apikeeper

import java.util.UUID

import apikeeper.model.Id
import apikeeper.service.internal.IdGenerator
import cats.Applicative
import cats.syntax.applicative._

class FixedUUID[F[_]: Applicative] extends IdGenerator[F] {
  val uuidString = "a64225eb-9737-4d80-bd9d-1ffe5fdb63b1"

  override def next(): F[Id] = Id(UUID.randomUUID()).pure

  def fixedUUID(): F[UUID] = UUID.fromString(uuidString).pure

  def randomUUID(): F[UUID] = UUID.randomUUID().pure
}

object FixedUUID {
  def apply[F[_]: Applicative](): FixedUUID[F] = new FixedUUID[F]()
}
