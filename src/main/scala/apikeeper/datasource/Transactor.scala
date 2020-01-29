package apikeeper.datasource

import apikeeper.datasource.Transactor.{Task, Tx}
import cats.data.ReaderT
import cats.effect.{Bracket, Sync}
import cats.syntax.applicative._
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import org.neo4j.driver.{Driver, Query, Result, Session, Transaction}
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

class Transactor[F[_]](driver: Driver)(implicit F: Sync[F]) extends LazyLogging {
  def transactSync[A](tx: Tx[F, A]): F[A] = {
    def acquire: F[Session] = F.delay(driver.session())

    def use(session: Session): F[A] = for {
      transaction <- session.beginTransaction().pure
      result <- tx(transaction).onError {
        case e: Throwable =>
          for {
            _ <- Logger[F].error("Error during transaction", e)
            _ <- transaction.rollback().pure
          } yield ()
      }
      _ <- transaction.commit().pure
    } yield result

    def release(session: Session): F[Unit] = F.delay(session.close())

    Bracket[F, Throwable].bracket(acquire)(use)(release)
  }

  def runner: Task[F] = query => runner0(query)

  private def runner0(query: Query): Tx[F, Result] = ReaderT { tx =>
    F.delay(tx.run(query))
  }
}

object Transactor {
  type Tx[F[_], A] = ReaderT[F, Transaction, A]

  type Task[F[_]] = Query => Tx[F, Result]

  def apply[F[_]: Sync](driver: Driver): Transactor[F] = new Transactor(driver)
}
