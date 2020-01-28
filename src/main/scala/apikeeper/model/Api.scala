package apikeeper.model

import org.neo4j.driver.Record

final case class Api(id: Id)

object Api {
  def fromRecord(record: Record): Api = ???
}
