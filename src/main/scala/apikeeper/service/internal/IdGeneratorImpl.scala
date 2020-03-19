package apikeeper.service.internal

import java.util.UUID

import apikeeper.model.Id
import cats.syntax.applicative._
import cats.Applicative

class IdGeneratorImpl[F[_]: Applicative] extends IdGenerator[F] {
  override def next(): F[Id] = Id(UUID.randomUUID()).pure
}

object IdGeneratorImpl {
  def apply[F[_]: Applicative](): IdGeneratorImpl[F] = new IdGeneratorImpl()
}
