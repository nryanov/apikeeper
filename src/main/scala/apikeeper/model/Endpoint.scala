package apikeeper.model

import org.neo4j.driver.Record

final case class Endpoint(id: Id)

object Endpoint {
  def fromRecord(record: Record): Endpoint = ???
}
