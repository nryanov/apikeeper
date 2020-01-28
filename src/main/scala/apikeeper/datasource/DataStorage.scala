package apikeeper.datasource

import apikeeper.Neo4jSettings
import apikeeper.datasource.DataStorage.{Transact, Tx}
import cats.effect.{Resource, Sync}
import cats.data.ReaderT
import org.neo4j.driver.{Driver, GraphDatabase, Query, Result, Transaction}

class DataStorage[F[_]](neo4jSettings: Neo4jSettings)(implicit F: Sync[F]) {
  def runner: Transact[F] = query => run(query)

  def run(query: Query): Tx[F, Result] = ReaderT { tx =>
    F.delay(tx.run(query))
  }

  def connect(): Resource[F, Driver] = {
    def acquire: F[Driver] = F.delay(GraphDatabase.driver(neo4jSettings.uri))

    def release(driver: Driver): F[Unit] = F.delay(driver.close())

    Resource.make(acquire)(release)
  }
}

object DataStorage {
  type Tx[F[_], A] = ReaderT[F, Transaction, A]

  type Transact[F[_]] = Query => Tx[F, Result]
}
