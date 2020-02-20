package apikeeper.model.syntax

import org.neo4j.driver.Record
import cats.syntax.option._
import org.neo4j.driver.types.Node

trait Neo4jRecordSyntax {
  implicit final def recordSyntax(a: Record): Neo4jRecordOps = new Neo4jRecordOps(a)
}

trait Neo4jNodeSyntax {
  implicit final def nodeSyntax(a: Node): Neo4jNodeOps = new Neo4jNodeOps(a)
}

final class Neo4jRecordOps(private val record: Record) extends AnyVal {
  def asString(field: String) = record.get(field).asString()

  def asString(field: String, default: String) = record.get(field).asString(default)

  def node(field: String): Node = record.get(field).asNode()

  def asOptionalString(field: String): Option[String] = record.get(field) match {
    case a if a.isNull => None
    case a             => a.asString().some
  }

  def asOptionalString(field: String, default: String): Option[String] = record.get(field) match {
    case a if a.isNull => default.some
    case a             => a.asString(default).some
  }
}

final class Neo4jNodeOps(private val node: Node) extends AnyVal {
  def asString(field: String) = node.get(field).asString()

  def asString(field: String, default: String) = node.get(field).asString(default)

  def asOptionalString(field: String): Option[String] = node.get(field) match {
    case a if a.isNull => None
    case a             => a.asString().some
  }

  def asOptionalString(field: String, default: String): Option[String] = node.get(field) match {
    case a if a.isNull => default.some
    case a             => a.asString(default).some
  }
}
