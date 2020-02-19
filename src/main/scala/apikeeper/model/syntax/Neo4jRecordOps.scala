package apikeeper.model.syntax

import org.neo4j.driver.Record
import cats.syntax.option._

trait Neo4jRecordSyntax {
  implicit final def recordSyntax(a: Record): Neo4jRecordOps = new Neo4jRecordOps(a)
}

final class Neo4jRecordOps(private val record: Record) extends AnyVal {
  def asString(field: String) = record.get(field).asString()

  def asString(field: String, default: String) = record.get(field).asString(default)

  def asOptionalString(field: String): Option[String] = record.get(field) match {
    case a if a.isNull => None
    case a             => a.asString().some
  }

  def asOptionalString(field: String, default: String): Option[String] = record.get(field) match {
    case a if a.isNull => default.some
    case a             => a.asString(default).some
  }
}
