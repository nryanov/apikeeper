package apikeeper.service

import apikeeper.datasource.Transactor
import apikeeper.datasource.Transactor.Tx
import apikeeper.model
import apikeeper.model.{Entity, Relation}
import apikeeper.model.graph.{Branch, Leaf}
import apikeeper.repository.KeeperRepository
import cats.effect.Sync
import cats.~>

class KeeperService[F[_]: Sync](
  transactor: Transactor[F],
  repository: KeeperRepository[F]
) extends Service[F] {
  private val transact: Tx[F, *] ~> F = transactor.transact()

  override def findEntity(entityId: model.Id): F[Option[Entity]] =
    transact(repository.findEntity(entityId))

  override def findEntitiesByNameLike(pattern: String, limit: Option[Int]): F[Seq[Entity]] =
    transact(repository.findEntitiesByNameLike(pattern, limit))

  override def findEntities(page: Int, countPerPage: Int): F[Seq[Entity]] =
    transact(repository.findEntities(page, countPerPage))

  override def findClosestEntityRelations(entityId: model.Id): F[Seq[Leaf]] =
    transact(repository.findClosestEntityRelations(entityId))

  override def createEntity(entity: Entity): F[Entity] =
    transact(repository.createEntity(entity))

  override def createEntities(entities: Seq[Entity]): F[Seq[Entity]] =
    transact(
      entities.toList
        .map(repository.createEntity(_).map(List(_)))
        .reduce[Tx[F, List[Entity]]] {
          case (l, r) => l.flatMap(r1 => r.map(r2 => r1 ::: r2))
        }
        .map(_.toSeq)
    )

  override def createRelation(branch: Branch): F[Relation] =
    transact(repository.createRelation(branch))

  override def createRelations(branches: Seq[Branch]): F[Seq[Relation]] =
    transact(
      branches.toList
        .map(repository.createRelation(_).map(List(_)))
        .reduce[Tx[F, List[Relation]]] {
          case (l, r) => l.flatMap(r1 => r.map(r2 => r1 ::: r2))
        }
        .map(_.toSeq)
    )

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
  def apply[F[_]: Sync](transactor: Transactor[F], repository: KeeperRepository[F]): KeeperService[F] =
    new KeeperService(transactor, repository)
}
