package apikeeper.datasource

import apikeeper.Neo4jSettings
import cats.effect.{Resource, Sync}
import org.neo4j.driver.{AuthToken, AuthTokens, Config, Driver, GraphDatabase, Logging}

class DataStorage[F[_]](neo4jSettings: Neo4jSettings)(implicit F: Sync[F]) {
  def connect(): Resource[F, Driver] = {
    def acquire: F[Driver] = {
      val auth: AuthToken = AuthTokens.basic(neo4jSettings.username, neo4jSettings.password)
      val cfg = Config.builder().withLogging(Logging.slf4j()).build()
      F.delay(GraphDatabase.driver(neo4jSettings.uri, auth, cfg))
    }

    def release(driver: Driver): F[Unit] = F.delay(driver.close())

    Resource.make(acquire)(release)
  }
}

object DataStorage {
  def apply[F[_]: Sync](neo4jSettings: Neo4jSettings): DataStorage[F] = new DataStorage(neo4jSettings)
}
