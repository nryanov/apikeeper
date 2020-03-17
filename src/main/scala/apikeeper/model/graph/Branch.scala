package apikeeper.model.graph

import apikeeper.model.{Id, Relation}

final case class Branch(left: Id, relation: Relation, right: Id)

object Branch {
  def apply(left: Id, relation: Relation, right: Id): Branch = new Branch(left, relation, right)

  def apply(id: Id, branchDef: BranchDef): Branch =
    new Branch(branchDef.left, Relation(id, branchDef.relationDef), branchDef.right)
}
