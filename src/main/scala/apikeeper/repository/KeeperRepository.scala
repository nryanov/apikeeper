package apikeeper.repository

import apikeeper.datasource.Transactor.{Task, Tx}
import apikeeper.model.{Entity, Id, Relation}
import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.option._
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.monad._
import cats.syntax.applicative._
import cats.syntax.show._
import cats.syntax.applicativeError._
import org.neo4j.driver.exceptions.NoSuchRecordException
import org.neo4j.driver.{Query, Values}
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

import scala.collection.JavaConverters._
import scala.util.control.NoStackTrace

class KeeperRepository[F[_]](
  transact: Task[F]
)(implicit F: Sync[F])
    extends Repository[Tx[F, *]]
    with LazyLogging {

  import KeeperRepository._

  override def findEntity(entityId: Id): Tx[F, Option[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entity by id: $entityId"))
    result <- transact(
      new Query(
        "MATCH (self:Entity {id: $id}) RETURN DISTINCT self.id, self.entityType, self.name, self.description, self.wikiLink",
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
    result <- transact(
      new Query(
        """
          |MATCH (:Entity {id: $id})-[self:Relation]-()
          |RETURN self.id
          |""".stripMargin,
        Values.parameters("id", entityId.show)
      )
    ).map(_.list().asScala.map(Relation.fromRecord).toSeq)
  } yield result

  override def createEntity(entity: Entity): Tx[F, Entity] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create entity: $entity"))
    _ <- transact(
      new Query(
        "CREATE (self:Entity {id: $id, entityType: $entityType, name: $name, description: $description, wikiLink: $wikiLink})",
        Entity.toValue(entity)
      )
    )
  } yield entity

  override def createRelation(from: Entity, to: Entity, relation: Relation): Tx[F, Relation] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create relation: $relation"))
    _ <- transact(
      new Query(
        """
          |MATCH (from:Entity {id: $fromId}), (to:Entity {id: $toId})
          |CREATE (from)-[:Relation {id: $id}]->(to)
          |""".stripMargin,
        Values.parameters("fromId", from.id.show, "toId", to.id.show, "id", relation.id.show)
      )
    )
  } yield relation

  override def removeEntity(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove entity: $entityId"))
    _ <- transact(
      new Query(
        "MATCH (entity:Entity {id: $id}) DETACH DELETE entity",
        Values.parameters("id", entityId.show)
      )
    )
  } yield ()

  override def removeAllEntityRelations(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove all entity's relations: $entityId"))
    _ <- transact(
      new Query(
        "MATCH (:Entity {id: $id})-[r:Relation]-() DELETE r",
        Values.parameters("id", entityId.show)
      )
    )
  } yield ()

  override def removeRelation(relationId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove relation: $relationId"))
    _ <- transact(
      new Query(
        "MATCH ()-[r:Relation {id: $id}]-() DELETE r",
        Values.parameters("id", relationId.show)
      )
    )
  } yield ()

  override def findEntities(page: Int, countPerPage: Int): Tx[F, Seq[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entities ($countPerPage): $page"))
    _ <- Kleisli.liftF(F.ensure(page.pure)(IncorrectPageNumber)(_ >= 0))
    _ <- Kleisli.liftF(F.ensure(countPerPage.pure)(IncorrectEntitiesPerPage)(_ >= 1))
    result <- transact(
      new Query(
        """
          |MATCH (self:Entity)
          |RETURN DISTINCT self.id, self.entityType, self.name, self.description, self.wikiLink
          |SKIP $skip LIMIT $limit
          |""".stripMargin,
        Values.parameters("skip", Int.box(countPerPage * (page - 1)), "limit", Int.box(countPerPage))
      )
    ).map(_.list().asScala.map(Entity.fromRecord).toSeq)
  } yield result
}

object KeeperRepository {
  def apply[F[_]: Sync](transact: Task[F]): KeeperRepository[F] = new KeeperRepository(transact)

  case object IncorrectEntitiesPerPage extends RuntimeException("Incorrect entities count per page") with NoStackTrace

  case object IncorrectPageNumber extends RuntimeException("Incorrect page number") with NoStackTrace
}
