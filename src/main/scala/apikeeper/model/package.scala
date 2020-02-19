package apikeeper

import java.util.UUID

import cats.Show

package object model {
  final case class Id(value: UUID)

  object Id {
    def apply(value: UUID): Id = new Id(value)

    def apply(value: String): Id = new Id(UUID.fromString(value))
  }

  implicit val idShow: Show[Id] = Show.show[Id](_.value.toString)
}
