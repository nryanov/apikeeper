package apikeeper.datasource

import apikeeper.Neo4jSettings
import cats.effect.{Resource, Sync}
import org.neo4j.driver.{Driver, GraphDatabase}

class DataStorage[F[_]](neo4jSettings: Neo4jSettings)(implicit F: Sync[F]) {
  def connect(): Resource[F, Driver] = {
    def acquire: F[Driver] = F.delay(GraphDatabase.driver(neo4jSettings.uri))

    def release(driver: Driver): F[Unit] = F.delay(driver.close())

    Resource.make(acquire)(release)
  }
}

object DataStorage {
  def apply[F[_]: Sync](neo4jSettings: Neo4jSettings): DataStorage[F] = new DataStorage(neo4jSettings)
}
