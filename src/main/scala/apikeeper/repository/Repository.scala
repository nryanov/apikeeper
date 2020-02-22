package apikeeper.repository

import apikeeper.model.graph.{Branch, Leaf}
import apikeeper.model.{Entity, Id, Relation}

trait Repository[F[_]] {
  def findEntity(entityId: Id): F[Option[Entity]]

  def findEntities(page: Int, countPerPage: Int = 10): F[Seq[Entity]]

  def findClosestEntityRelations(entityId: Id): F[Seq[Leaf]]

  def createEntity(entity: Entity): F[Entity]

  def createRelation(node: Branch): F[Relation]

  def removeEntity(entityId: Id): F[Unit]

  def removeAllEntityRelations(entityId: Id): F[Unit]

  def removeRelation(relationId: Id): F[Unit]
}
