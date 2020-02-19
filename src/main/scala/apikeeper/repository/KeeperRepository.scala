package apikeeper.repository

import apikeeper.datasource.Transactor.{Task, Tx}
import apikeeper.model.{Entity, Id, Relation}
import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.option._
import cats.syntax.show._
import cats.syntax.functor._
import cats.syntax.applicativeError._
import org.neo4j.driver.exceptions.NoSuchRecordException
import org.neo4j.driver.{Query, Values}
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

class KeeperRepository[F[_]](
  transact: Task[F]
)(implicit F: Sync[F])
    extends Repository[Tx[F, *]]
    with LazyLogging {

  override def findEntity(entityId: Id): Tx[F, Option[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entity by id: $entityId"))
    result <- transact(
      new Query(
        "MATCH (self:Entity {id: $id}) RETURN self.id, self.entityType, self.name, self.description, self.wikiLink",
        Values.parameters("id", entityId.show)
      )
    ).flatMapF(result =>
      F.catchNonFatal(Entity.fromRecord(result.single()).some).recover {
        case _: NoSuchRecordException => None
      }
    )
  } yield result

  override def findClosestEntityRelations(entityId: Id): Tx[F, Seq[Relation]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find closest relations for entity by id: $entityId"))
  } yield ???

  override def createEntity(entity: Entity): Tx[F, Entity] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create entity: $entity"))
    _ <- transact(
      new Query(
        "CREATE (self:Entity {id: $id, entityType: $entityType, name: $name, description: $description, wikiLink: $wikiLink})".stripMargin,
        Entity.toValue(entity)
      )
    )
  } yield entity

  override def createRelation(relation: Relation): Tx[F, Relation] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create relation: $relation"))
  } yield ???

  override def removeEntity(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove entity: $entityId"))
  } yield ???

  override def removeAllEntityRelations(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove all entity's relations: $entityId"))
  } yield ???

  override def removeRelation(relationId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove relation: $relationId"))
  } yield ???
}

object KeeperRepository {
  def apply[F[_]: Sync](transact: Task[F]): KeeperRepository[F] = new KeeperRepository(transact)
}
