package apikeeper.model

import org.neo4j.driver.Record

final case class Usage(id: Id)

object Usage {
  def fromRecord(record: Record): Usage = ???
}
