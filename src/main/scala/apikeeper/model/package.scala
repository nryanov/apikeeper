package apikeeper

import java.util.UUID

import cats.Show
import cats.syntax.OptionSyntax
import org.neo4j.driver.Value

package object model {
  final case class Id(value: UUID)

  object Id {
    def apply(value: UUID): Id = new Id(value)

    def apply(value: String): Id = new Id(UUID.fromString(value))
  }

  implicit val idShow: Show[Id] = Show.show[Id](_.value.toString)

  implicit object Neo4jOptionSyntax extends OptionSyntax {
    implicit final def valueSyntaxOptionId(a: Value): Neo4jOptionIdOps = new Neo4jOptionIdOps(a)
  }

  final class Neo4jOptionIdOps(private val a: Value) extends AnyVal {

    def optionalValue: Option[Value] = if (a.isNull) None else Some(a)
  }
}
