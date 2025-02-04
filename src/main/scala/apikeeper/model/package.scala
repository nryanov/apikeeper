package apikeeper

import cats.Show
import cats.syntax.show._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import java.util.UUID

import apikeeper.model.graph.{Branch, BranchDef, Leaf}
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder}

package object model {
  final case class Id(id: UUID)

  object Id {
    def apply(value: UUID): Id = new Id(value)

    def apply(value: String): Id = new Id(UUID.fromString(value))
  }

  implicit val idShow: Show[Id] = Show.show[Id](_.id.toString)

  implicit val idEncoder: Encoder[Id] = (a: Id) => Json.fromString(a.show)
  implicit val idDecoder: Decoder[Id] = (c: HCursor) =>
    for {
      id <- c.as[String]
    } yield Id(id)

  implicit val entityEncoder: Encoder[Entity] = deriveEncoder[Entity]
  implicit val entityDecoder: Decoder[Entity] = deriveDecoder[Entity]

  implicit val entityDefEncoder: Encoder[EntityDef] = deriveEncoder[EntityDef]
  implicit val entityDefDecoder: Decoder[EntityDef] = deriveDecoder[EntityDef]

  implicit val relationEncoder: Encoder[Relation] = deriveEncoder[Relation]
  implicit val relationDecoder: Decoder[Relation] = deriveDecoder[Relation]

  implicit val relationDefEncoder: Encoder[RelationDef] = deriveEncoder[RelationDef]
  implicit val relationDefDecoder: Decoder[RelationDef] = deriveDecoder[RelationDef]

  implicit val branchEncoder: Encoder[Branch] = deriveEncoder[Branch]
  implicit val branchDecoder: Decoder[Branch] = deriveDecoder[Branch]

  implicit val branchDefEncoder: Encoder[BranchDef] = deriveEncoder[BranchDef]
  implicit val branchDefDecoder: Decoder[BranchDef] = deriveDecoder[BranchDef]

  implicit val leafEncoder: Encoder[Leaf] = deriveEncoder[Leaf]
  implicit val leafDecoder: Decoder[Leaf] = deriveDecoder[Leaf]
}
