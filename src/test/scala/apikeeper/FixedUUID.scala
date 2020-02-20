package apikeeper

import java.util.UUID

import cats.Monad
import cats.syntax.applicative._

class FixedUUID[F[_]: Monad] {
  val uuidString = "a64225eb-9737-4d80-bd9d-1ffe5fdb63b1"

  def fixedUUID(): F[UUID] = UUID.fromString(uuidString).pure

  def randomUUI(): F[UUID] = UUID.randomUUID().pure
}

object FixedUUID {
  def apply[F[_]: Monad](): FixedUUID[F] = new FixedUUID[F]()
}
