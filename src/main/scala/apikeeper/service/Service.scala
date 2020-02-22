package apikeeper.service

import apikeeper.model.{Entity, Id, Relation}
import apikeeper.model.graph.{Branch, Leaf}

trait Service[F[_]] {
  def findEntity(entityId: Id): F[Option[Entity]]

  def findEntities(page: Int, countPerPage: Int = 10): F[Seq[Entity]]

  def findClosestEntityRelations(entityId: Id): F[Seq[Leaf]]

  def createEntity(entity: Entity): F[Entity]

  def createEntities(entities: Seq[Entity]): F[Seq[Entity]]

  def createRelation(branch: Branch): F[Relation]

  def createRelations(branches: Seq[Branch]): F[Seq[Relation]]

  def removeEntity(entityId: Id): F[Unit]

  def removeEntities(entityIds: Seq[Id]): F[Unit]

  def removeAllEntityRelations(entityId: Id): F[Unit]

  def removeRelation(relationId: Id): F[Unit]

  def removeRelations(relationIds: Seq[Id]): F[Unit]
}
