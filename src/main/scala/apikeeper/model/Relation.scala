package apikeeper.model

final case class Relation(
  id: Id,
  relationType: RelationType,
  from: Entity,
  to: Entity
)
