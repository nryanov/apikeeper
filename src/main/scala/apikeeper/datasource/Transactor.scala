package apikeeper.datasource

import apikeeper.datasource.Transactor.Tx
import cats.arrow.FunctionK
import cats.data.ReaderT
import cats.~>
import cats.effect.{Blocker, Bracket, ContextShift, Sync}
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.applicativeError._
import distage.Id
import org.neo4j.driver.{Driver, Session, Transaction}
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

class Transactor[F[_]: ContextShift](driver: Driver, blocker: Blocker @Id("transactionBlocker"))(implicit F: Sync[F]) extends LazyLogging {
  def transact(): ~>[Tx[F, *], F] =
    FunctionK.lift[Tx[F, *], F](transactSync)

  private def transactSync[A](tx: Tx[F, A]): F[A] = {
    def acquire: F[Session] = F.delay(driver.session())

    def use(session: Session): F[A] = blocker.blockOn(run(session))

    def run(session: Session): F[A] = for {
      transaction <- F.delay(session.beginTransaction())
      result <- tx(transaction).onError {
        case e: Throwable =>
          for {
            _ <- Logger[F].error("Error during transaction", e)
            _ <- F.delay(transaction.rollback())
          } yield ()
      }
      _ <- F.delay(transaction.commit())
    } yield result

    def release(session: Session): F[Unit] = F.delay(session.close())

    Bracket[F, Throwable].bracket(acquire)(use)(release)
  }
}

object Transactor {
  type Tx[F[_], A] = ReaderT[F, Transaction, A]

  def apply[F[_]: Sync: ContextShift](driver: Driver, blocker: Blocker): Transactor[F] = new Transactor(driver, blocker)
}
