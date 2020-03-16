package apikeeper.model

import cats.syntax.show._
import org.neo4j.driver.{Record, Value, Values}
import apikeeper.model.syntax.Neo4jSyntax._
import org.neo4j.driver.types.Node

final case class Entity(
  id: Id,
  entityType: EntityType,
  name: String,
  description: Option[String] = None
)

object Entity {
  def fromRecord(record: Record, ref: String = "self"): Entity = {
    val id = Id(record.asString(s"$ref.id"))
    val entityType = EntityType.withNameInsensitive(record.asString(s"$ref.entityType"))
    val name = record.asString(s"$ref.name")
    val description = record.asOptionalString(s"$ref.description")

    new Entity(id, entityType, name, description)
  }

  def fromNode(node: Node, ref: String = "self"): Entity = {
    val id = Id(node.asString(s"$ref.id"))
    val entityType = EntityType.withNameInsensitive(node.asString(s"$ref.entityType"))
    val name = node.asString(s"$ref.name")
    val description = node.asOptionalString(s"$ref.description")

    new Entity(id, entityType, name, description)
  }

  def toValue(entity: Entity): Value = Values.parameters(
    "id",
    entity.id.show,
    "entityType",
    entity.entityType.entryName,
    "name",
    entity.name,
    "description",
    entity.description.orNull
  )
}
