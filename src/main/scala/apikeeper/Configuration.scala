package apikeeper

final case class Neo4jSettings(
  uri: String
)

final case class ServerSettings()

final case class Configuration(
  neo4jSettings: Neo4jSettings,
  serverSettings: ServerSettings
)

object Configuration {
  def create[F[_]]() = ???
}
