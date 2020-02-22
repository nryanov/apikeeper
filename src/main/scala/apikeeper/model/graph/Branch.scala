package apikeeper.model.graph

import apikeeper.model.{Id, Relation}

final case class Branch(left: Id, relation: Relation, right: Id)
