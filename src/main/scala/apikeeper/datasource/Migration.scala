package apikeeper.datasource

import apikeeper.datasource.Transactor.Tx
import cats.data.Kleisli
import cats.effect.Sync
import cats.~>
import org.neo4j.driver.Query
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

class Migration[F[_]](transactor: Transactor[F], queryRunner: QueryRunner[F])(implicit F: Sync[F]) extends LazyLogging {
  private val transact: Tx[F, *] ~> F = transactor.transact()

  def migrate(): F[Unit] = transact(for {
    _ <- createEntityConstraint()
    _ <- createRelationConstraint()
  } yield ())

  private def createEntityConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Create unique constraint for entity"))
      _ <- queryRunner.run(new Query("CREATE CONSTRAINT ON (e:Entity) ASSERT e.id IS UNIQUE"))
      _ <- Kleisli.liftF(Logger[F].info("Entity constraint was created"))
    } yield ()

  private def createRelationConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Create unique constraint for relation"))
      _ <- queryRunner.run(new Query("CREATE CONSTRAINT ON (r:Relation) ASSERT r.id IS UNIQUE"))
      _ <- Kleisli.liftF(Logger[F].info("Relation unique constraint was created"))
    } yield ()
}

object Migration {
  def apply[F[_]: Sync](transactor: Transactor[F], queryRunner: QueryRunner[F]): Migration[F] =
    new Migration(transactor, queryRunner)
}
