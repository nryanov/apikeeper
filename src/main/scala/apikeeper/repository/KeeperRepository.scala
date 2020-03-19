package apikeeper.repository

import apikeeper.datasource.QueryRunner
import apikeeper.datasource.Transactor.Tx
import apikeeper.model.graph.{Branch, Leaf}
import apikeeper.model.{Entity, Id, Relation, RelationType}
import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.option._
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
  queryRunner: QueryRunner[F]
)(implicit F: Sync[F])
    extends Repository[Tx[F, *]]
    with LazyLogging {

  import KeeperRepository._

  override def findEntity(entityId: Id): Tx[F, Option[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entity by id: $entityId"))
    result <- queryRunner
      .run(
        new Query(
          "MATCH (self:Entity {id: $id}) RETURN DISTINCT self.id, self.entityType, self.name, self.description",
          Values.parameters("id", entityId.show)
        )
      )
      .flatMapF(result =>
        F.catchNonFatal(Entity.fromRecord(result.single()).some).recover {
          case _: NoSuchRecordException => None
        }
      )
  } yield result

  override def findEntitiesByNameLike(pattern: String, limit: Int = 10): Tx[F, Seq[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entities by name: $pattern"))
    result <- queryRunner
      .run(
        new Query(
          """
            |MATCH (self:Entity)
            |WHERE self.name =~ $pattern
            |RETURN DISTINCT self.id, self.entityType, self.name, self.description
            |LIMIT $limit
            |""".stripMargin,
          Values.parameters("pattern", s".*?$pattern.*?", "limit", Int.box(limit))
        )
      )
      .map(_.list().asScala.map(Entity.fromRecord(_)).toSeq)
  } yield result

  override def findClosestEntityRelations(entityId: Id): Tx[F, Seq[Leaf]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find closest relations for entity by id: $entityId"))
    result <- queryRunner
      .run(
        new Query(
          """
          |MATCH (:Entity {id: $id})-[rel:Relation]-(e:Entity)
          |RETURN rel.id, rel.relationType, e.id, e.entityType, e.name, e.description
          |""".stripMargin,
          Values.parameters("id", entityId.show)
        )
      )
      .map(
        _.list().asScala.map { record =>
          println(record)
          Leaf(
            entity = Entity.fromRecord(record, "e"),
            relation = Relation.fromRecord(record, "rel")
          )
        }
      )
  } yield result

  override def createEntity(entity: Entity): Tx[F, Entity] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create entity: $entity"))
    _ <- queryRunner.run(
      new Query(
        "CREATE (self:Entity {id: $id, entityType: $entityType, name: $name, description: $description})",
        Entity.toValue(entity)
      )
    )
  } yield entity

  override def createRelation(node: Branch): Tx[F, Relation] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Create relation: $node"))
    relation = node.relation
    (from, to) = relation.relationType match {
      case RelationType.In  => (node.right, node.left)
      case RelationType.Out => (node.left, node.right)
    }
    _ <- queryRunner.run(
      new Query(
        """
          |MATCH (from:Entity {id: $fromId}), (to:Entity {id: $toId})
          |CREATE (from)-[:Relation {id: $id, relationType: $relationType}]->(to)
          |""".stripMargin,
        Values.parameters(
          "fromId",
          from.show,
          "toId",
          to.show,
          "id",
          relation.id.show,
          "relationType",
          relation.relationType.entryName
        )
      )
    )
  } yield relation

  override def removeEntity(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove entity: $entityId"))
    _ <- queryRunner.run(
      new Query(
        "MATCH (entity:Entity {id: $id}) DETACH DELETE entity",
        Values.parameters("id", entityId.show)
      )
    )
  } yield ()

  override def removeAllEntityRelations(entityId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove all entity's relations: $entityId"))
    _ <- queryRunner.run(
      new Query(
        "MATCH (:Entity {id: $id})-[r:Relation]-() DELETE r",
        Values.parameters("id", entityId.show)
      )
    )
  } yield ()

  override def removeRelation(relationId: Id): Tx[F, Unit] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Remove relation: $relationId"))
    _ <- queryRunner.run(
      new Query(
        "MATCH ()-[r:Relation {id: $id}]-() DELETE r",
        Values.parameters("id", relationId.show)
      )
    )
  } yield ()

  override def findEntities(page: Int, countPerPage: Int = 10): Tx[F, Seq[Entity]] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Find entities ($countPerPage): $page"))
    _ <- Kleisli.liftF(F.ensure(page.pure)(IncorrectPageNumber)(_ >= 0))
    _ <- Kleisli.liftF(F.ensure(countPerPage.pure)(IncorrectEntitiesPerPage)(_ >= 1))
    result <- queryRunner
      .run(
        new Query(
          """
          |MATCH (self:Entity)
          |RETURN DISTINCT self.id, self.entityType, self.name, self.description
          |SKIP $skip LIMIT $limit
          |""".stripMargin,
          Values.parameters("skip", Int.box(countPerPage * (page - 1)), "limit", Int.box(countPerPage))
        )
      )
      .map(_.list().asScala.map(Entity.fromRecord(_)).toSeq)
  } yield result
}

object KeeperRepository {
  def apply[F[_]: Sync](queryRunner: QueryRunner[F]): KeeperRepository[F] = new KeeperRepository(queryRunner)

  case object IncorrectEntitiesPerPage extends RuntimeException("Incorrect entities count per page") with NoStackTrace

  case object IncorrectPageNumber extends RuntimeException("Incorrect page number") with NoStackTrace
}
