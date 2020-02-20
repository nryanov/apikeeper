package apikeeper.model

import org.neo4j.driver.Record
import apikeeper.model.syntax.Neo4jSyntax._

final case class Relation(id: Id)

object Relation {
  def fromRecord(record: Record): Relation = {
    val id = Id(record.asString("self.id"))

    new Relation(id)
  }
}
