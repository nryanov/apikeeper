package apikeeper

import cats.effect.Sync
import com.typesafe.config.Config
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig.generic.auto._

final case class Neo4jSettings(
  uri: String,
  username: String,
  password: String
)

final case class ServerSettings(
  port: Int,
  host: String
)

final case class Configuration(
  neo4jSettings: Neo4jSettings,
  serverSettings: ServerSettings
)

object Configuration {
  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def create[F[_]](implicit F: Sync[F]): F[Configuration] =
    F.delay(ConfigSource.default.at("apikeeper").loadOrThrow[Configuration])

  def create[F[_]](value: String)(implicit F: Sync[F]): F[Configuration] =
    F.delay(ConfigSource.string(value).at("apikeeper").loadOrThrow[Configuration])

  def create[F[_]](value: Config)(implicit F: Sync[F]): F[Configuration] =
    F.delay(ConfigSource.fromConfig(value).at("apikeeper").loadOrThrow[Configuration])
}
