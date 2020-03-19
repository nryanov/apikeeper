package apikeeper.model.graph

import apikeeper.model.{Id, RelationDef}

final case class BranchDef(left: Id, relationDef: RelationDef, right: Id)
