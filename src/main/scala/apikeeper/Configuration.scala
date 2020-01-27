package apikeeper

final case class Neo4jConnection()

final case class ServerSettings()

final case class Configuration()

object Configuration {
  def create[F[_]]() = ???
}
