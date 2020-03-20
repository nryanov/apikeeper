package apikeeper.service

import cats.~>
import cats.effect.Sync
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.traverse._
import cats.instances.list._
import apikeeper.datasource.Transactor
import apikeeper.datasource.Transactor.Tx
import apikeeper.model
import apikeeper.model.{Entity, EntityDef, Relation}
import apikeeper.model.graph.{Branch, BranchDef, Leaf}
import apikeeper.repository.KeeperRepository
import apikeeper.service.internal.IdGenerator

class KeeperService[F[_]: Sync](
  transactor: Transactor[F],
  repository: KeeperRepository[F],
  idGenerator: IdGenerator[F]
) extends Service[F] {
  private val transact: Tx[F, *] ~> F = transactor.transact()

  override def findEntity(entityId: model.Id): F[Option[Entity]] =
    transact(repository.findEntity(entityId))

  override def findEntitiesByNameLike(pattern: String, limit: Int = 10): F[Seq[Entity]] =
    transact(repository.findEntitiesByNameLike(pattern, limit))

  override def findEntities(page: Int, countPerPage: Int = 10): F[Seq[Entity]] =
    transact(repository.findEntities(page, countPerPage))

  override def findClosestEntityRelations(entityId: model.Id): F[Seq[Leaf]] =
    transact(repository.findClosestEntityRelations(entityId))

  override def createEntity(entityDef: EntityDef): F[Entity] = for {
    id <- idGenerator.next()
    entity = Entity(id, entityDef)
    _ <- transact(repository.createEntity(entity))
  } yield entity

  override def updateEntity(entity: Entity): F[Entity] =
    transact(repository.updateEntity(entity))

  override def createEntities(entityDefs: Seq[EntityDef]): F[Seq[Entity]] =
    for {
      entities <- entityDefs.toList.traverse(entityDef => idGenerator.next().map(id => Entity(id, entityDef)))
      _ <- transact(entities.map(repository.createEntity(_).map(List(_))).reduce[Tx[F, List[Entity]]] {
        case (l, r) => l.flatMap(r1 => r.map(r2 => r1 ::: r2))
      })
    } yield entities

  override def createRelation(branchDef: BranchDef): F[Relation] =
    for {
      id <- idGenerator.next()
      branch = Branch(id, branchDef)
      relation <- transact(repository.createRelation(branch))
    } yield relation

  override def createRelations(branchDefs: Seq[BranchDef]): F[Seq[Relation]] =
    for {
      branches <- branchDefs.toList.traverse(branchDef => idGenerator.next().map(id => Branch(id, branchDef)))
      relations <- transact(branches.map(repository.createRelation(_).map(List(_))).reduce[Tx[F, List[Relation]]] {
        case (l, r) => l.flatMap(r1 => r.map(r2 => r1 ::: r2))
      })
    } yield relations

  override def removeEntity(entityId: model.Id): F[Unit] =
    transact(repository.removeEntity(entityId))

  override def removeEntities(entityIds: Seq[model.Id]): F[Unit] =
    transact(entityIds.toList.map(repository.removeEntity).reduce[Tx[F, Unit]] {
      case (l, r) => l.flatMap(_ => r)
    })

  override def removeAllEntityRelations(entityId: model.Id): F[Unit] =
    transact(repository.removeAllEntityRelations(entityId))

  override def removeRelation(relationId: model.Id): F[Unit] =
    transact(repository.removeRelation(relationId))

  override def removeRelations(relationIds: Seq[model.Id]): F[Unit] =
    transact(relationIds.toList.map(repository.removeRelation).reduce[Tx[F, Unit]] {
      case (l, r) => l.flatMap(_ => r)
    })
}

object KeeperService {
  def apply[F[_]: Sync](transactor: Transactor[F], repository: KeeperRepository[F], idGenerator: IdGenerator[F]): KeeperService[F] =
    new KeeperService(transactor, repository, idGenerator)
}
