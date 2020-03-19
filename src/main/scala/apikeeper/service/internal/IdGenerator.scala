package apikeeper.service.internal

import apikeeper.model.Id

trait IdGenerator[F[_]] {
  def next(): F[Id]
}
