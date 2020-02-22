package apikeeper.datasource

import apikeeper.datasource.Transactor.Tx
import cats.data.ReaderT
import cats.effect.Sync
import org.neo4j.driver.{Query, Result}

class QueryRunner[F[_]](implicit F: Sync[F]) {
  import QueryRunner._

  def run: Task[F] = query => runner0(query)

  private def runner0(query: Query): Tx[F, Result] = ReaderT { tx =>
    F.delay(tx.run(query))
  }
}

object QueryRunner {
  def apply[F[_]: Sync](): QueryRunner[F] = new QueryRunner()

  type Task[F[_]] = Query => Tx[F, Result]
}
