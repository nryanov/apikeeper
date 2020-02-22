package apikeeper.model.graph

import apikeeper.model.{Entity, Relation}

final case class Leaf(entity: Entity, relation: Relation)
