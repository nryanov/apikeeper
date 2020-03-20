package apikeeper.service

import apikeeper.model.{Entity, EntityDef, Id, Relation}
import apikeeper.model.graph.{Branch, BranchDef, Leaf}

trait Service[F[_]] {
  def findEntity(entityId: Id): F[Option[Entity]]

  def findEntities(page: Int, countPerPage: Int): F[Seq[Entity]]

  def findEntitiesByNameLike(pattern: String, limit: Int): F[Seq[Entity]]

  def findClosestEntityRelations(entityId: Id): F[Seq[Leaf]]

  def createEntity(entityDef: EntityDef): F[Entity]

  def updateEntity(entity: Entity): F[Entity]

  def createEntities(entityDefs: Seq[EntityDef]): F[Seq[Entity]]

  def createRelation(branchDef: BranchDef): F[Relation]

  def createRelations(branchDefs: Seq[BranchDef]): F[Seq[Relation]]

  def removeEntity(entityId: Id): F[Unit]

  def removeEntities(entityIds: Seq[Id]): F[Unit]

  def removeAllEntityRelations(entityId: Id): F[Unit]

  def removeRelation(relationId: Id): F[Unit]

  def removeRelations(relationIds: Seq[Id]): F[Unit]
}
