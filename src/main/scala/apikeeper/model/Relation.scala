package apikeeper.model

import org.neo4j.driver.Record
import apikeeper.model.syntax.Neo4jSyntax._
import org.neo4j.driver.types.Node

final case class Relation(id: Id, relationType: RelationType)

object Relation {
  def apply(id: Id, relationType: RelationType): Relation = new Relation(id, relationType)

  def apply(id: Id, relationDef: RelationDef): Relation = new Relation(id, relationDef.relationType)

  def fromRecord(record: Record, ref: String = "self"): Relation = {
    val id = Id(record.asString(s"$ref.id"))
    val relationType = RelationType.withNameInsensitive(record.asString(s"relationType"))

    new Relation(id, relationType)
  }

  def fromNode(node: Node, ref: String = "self"): Relation = {
    val id = Id(node.asString(s"$ref.id"))
    val relationType = RelationType.withNameInsensitive(node.asString(s"relationType"))

    new Relation(id, relationType)
  }
}
