package apikeeper.model

import cats.syntax.show._
import org.neo4j.driver.{Record, Value, Values}
import apikeeper.model.syntax.Neo4jSyntax._

final case class Entity(
  id: Id,
  entityType: EntityType,
  name: String,
  description: Option[String] = None,
  wikiLink: Option[String] = None
)

object Entity {
  def fromRecord(record: Record): Entity = {
    val id = Id(record.asString("self.id"))
    val entityType = EntityType.withNameInsensitive(record.asString("self.entityType"))
    val name = record.asString("self.name")
    val description = record.asOptionalString("self.description")
    val wikiLink = record.asOptionalString("self.wikiLink")

    new Entity(id, entityType, name, description, wikiLink)
  }

  def toValue(entity: Entity): Value = Values.parameters(
    "id",
    entity.id.show,
    "entityType",
    entity.entityType.entryName,
    "name",
    entity.name,
    "description",
    entity.description.orNull,
    "wikiLink",
    entity.wikiLink.orNull
  )
}
