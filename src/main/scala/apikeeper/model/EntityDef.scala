package apikeeper.model

final case class EntityDef(
  entityType: EntityType,
  name: String,
  description: Option[String] = None
)
