package apikeeper.model

import org.neo4j.driver.Record
import Neo4jOptionSyntax._

final case class Api(id: Id, name: String, description: Option[String] = None, wikiLink: Option[String] = None)

object Api {
  def fromRecord(record: Record): Api = {
    val id = Id(record.get("self.id").asString())
    val name = record.get("self.name").asString()
    val description = record.get("self.description").optionalValue.map(_.asString())
    val wikiLink = record.get("self.wikiLink").optionalValue.map(_.asString())

    new Api(id, name, description, wikiLink)
  }
}
