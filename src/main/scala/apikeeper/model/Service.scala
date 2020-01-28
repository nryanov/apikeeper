package apikeeper.model

import org.neo4j.driver.Record

final case class Service(id: Id)

object Service {
  def fromRecord(record: Record): Service = ???
}
