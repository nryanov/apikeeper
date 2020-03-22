package apikeeper.http.internal

import apikeeper.model.EntityType

sealed trait Filter

final case class NameFilter(pattern: String, entries: Int) extends Filter

final case class EntityTypeFilter(entityType: EntityType) extends Filter
