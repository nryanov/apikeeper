package apikeeper.datasource

import apikeeper.datasource.Transactor.Tx
import cats.data.Kleisli
import cats.syntax.applicativeError._
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
    _ <- createEntityNameIndex()
  } yield ())

  def clean(): F[Unit] = transact(for {
    _ <- dropEntityConstraint()
    _ <- dropRelationConstraint()
    _ <- dropEntityNameIndex()
  } yield ()).handleError(_ => ()) // if constraint does not exist yet

  def truncate(): F[Unit] = transact(clearDatabase())

  private def clearDatabase(): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info("Clear DB"))
    _ <- queryRunner.run(new Query("match (n) detach delete n"))
  } yield ()

  private def createEntityConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Create unique constraint for entity"))
      _ <- queryRunner.run(new Query("CREATE CONSTRAINT entity_unique_id ON (e:Entity) ASSERT e.id IS UNIQUE"))
      _ <- Kleisli.liftF(Logger[F].info("Entity constraint was created"))
    } yield ()

  private def createRelationConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Create unique constraint for relation"))
      _ <- queryRunner.run(new Query("CREATE CONSTRAINT relation_unique_id ON (r:Relation) ASSERT r.id IS UNIQUE"))
      _ <- Kleisli.liftF(Logger[F].info("Relation unique constraint was created"))
    } yield ()

  private def createEntityNameIndex(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Create index for Entity:name"))
      _ <- queryRunner.run(new Query("CREATE INDEX entity_name_idx FOR (e:Entity) ON (e.name)"))
      _ <- Kleisli.liftF(Logger[F].info("Index for Entity:name was created"))
    } yield ()

  private def dropEntityConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Drop unique constraint for entity"))
      _ <- queryRunner.run(new Query("DROP CONSTRAINT entity_unique_id"))
      _ <- Kleisli.liftF(Logger[F].info("Entity constraint was dropped"))
    } yield ()

  private def dropRelationConstraint(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Drop unique constraint for relation"))
      _ <- queryRunner.run(new Query("DROP CONSTRAINT relation_unique_id"))
      _ <- Kleisli.liftF(Logger[F].info("Relation unique constraint was dropped"))
    } yield ()

  private def dropEntityNameIndex(): Tx[F, Unit] =
    for {
      _ <- Kleisli.liftF(Logger[F].info("Drop index for Entity:name"))
      _ <- queryRunner.run(new Query("DROP INDEX entity_name_idx"))
      _ <- Kleisli.liftF(Logger[F].info("Index for Entity:name was dropped"))
    } yield ()
}

object Migration {
  def apply[F[_]: Sync](transactor: Transactor[F], queryRunner: QueryRunner[F]): Migration[F] =
    new Migration(transactor, queryRunner)
}
