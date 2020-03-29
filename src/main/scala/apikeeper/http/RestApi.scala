package apikeeper.http

import apikeeper.http.internal.{EntityTypeFilter, Filter, NameFilter}
import apikeeper.model.{Entity, EntityDef, Id, Relation}
import apikeeper.model.graph.{BranchDef, Leaf}
import apikeeper.repository.KeeperRepository.{IncorrectEntitiesPerPage, IncorrectPageNumber}
import apikeeper.service.Service
import cats.effect.{ContextShift, Sync}
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.either._
import cats.syntax.semigroupk._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import io.circe.generic.auto._
import sttp.tapir.server.http4s._
import sttp.model.StatusCode

class RestApi[F[_]: ContextShift](service: Service[F])(implicit F: Sync[F]) {
  import RestApi._

  private val baseEndpoint: Endpoint[Unit, (StatusCode, ErrorInfo), Unit, Nothing] =
    endpoint.in("api" / apiVersion).errorOut(statusCode.and(jsonBody[ErrorInfo]))

  val findEntityEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Option[Entity], Nothing] =
    baseEndpoint.get.in("entity").in(path[Id]).out(jsonBody[Option[Entity]])

  val findEntityRoute = findEntityEndpoint.toRoutes(id => toRoute(service.findEntity(id)))

  val findEntitiesEndpoint: Endpoint[(Option[Int], Option[Int]), (StatusCode, ErrorInfo), Seq[Entity], Nothing] =
    baseEndpoint.get.in("entity").in(query[Option[Int]]("page")).in(query[Option[Int]]("entries")).out(jsonBody[Seq[Entity]])

  val findEntitiesRoute =
    findEntitiesEndpoint.toRoutes {
      case (Some(page), Some(count)) => toRoute(service.findEntities(page, count))
      case _                         => toRoute(service.findAllEntities())
    }

  val findEntitiesByFilterEndpoint: Endpoint[Filter, (StatusCode, ErrorInfo), Seq[Entity], Nothing] =
    baseEndpoint.get.in("entity" / "filter").in(jsonBody[Filter]).out(jsonBody[Seq[Entity]])

  val findEntitiesByFilterRoute =
    findEntitiesByFilterEndpoint.toRoutes {
      case filter: NameFilter       => toRoute(service.findEntitiesByNameLike(filter.pattern, filter.entries))
      case filter: EntityTypeFilter => toRoute(service.findEntitiesByType(filter.entityType))
    }

  val findClosestEntityRelationsEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Seq[Leaf], Nothing] =
    baseEndpoint.get.in("entity").in(path[Id]).in("relation").out(jsonBody[Seq[Leaf]])

  val findClosestEntityRelationsRoute =
    findClosestEntityRelationsEndpoint.toRoutes(id => toRoute(service.findClosestEntityRelations(id)))

  val createEntityEndpoint: Endpoint[EntityDef, (StatusCode, ErrorInfo), Entity, Nothing] =
    baseEndpoint.post.in("entity").in(jsonBody[EntityDef]).out(jsonBody[Entity])

  val createEntityRoute =
    createEntityEndpoint.toRoutes(entityDef => toRoute(service.createEntity(entityDef)))

  val updateEntityEndpoint: Endpoint[Entity, (StatusCode, ErrorInfo), Entity, Nothing] =
    baseEndpoint.put.in("entity").in(jsonBody[Entity]).out(jsonBody[Entity])

  val updateEntityRoute =
    updateEntityEndpoint.toRoutes(entity => toRoute(service.updateEntity(entity)))

  val createRelationEndpoint: Endpoint[BranchDef, (StatusCode, ErrorInfo), Relation, Nothing] =
    baseEndpoint.post.in("relation").in(jsonBody[BranchDef]).out(jsonBody[Relation])

  val createRelationRoute =
    createRelationEndpoint.toRoutes(branch => toRoute(service.createRelation(branch)))

  val removeEntityEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity").in(path[Id])

  val removeEntityRoute =
    removeEntityEndpoint.toRoutes(id => toRoute(service.removeEntity(id)))

  val removeEntitiesEndpoint: Endpoint[Seq[Id], (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity").in(jsonBody[Seq[Id]])

  val removeEntitiesRoute =
    removeEntitiesEndpoint.toRoutes(ids => toRoute(service.removeEntities(ids)))

  val removeAllEntityRelationsEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity").in(path[Id]).in("relation")

  val removeAllEntityRelationsRoute =
    removeAllEntityRelationsEndpoint.toRoutes(id => toRoute(service.removeAllEntityRelations(id)))

  val removeRelationEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("relation").in(path[Id])

  val removeRelationRoute =
    removeRelationEndpoint.toRoutes(id => toRoute(service.removeRelation(id)))

  val removeRelationsEndpoint: Endpoint[Seq[Id], (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("relation").in(jsonBody[Seq[Id]])

  val removeRelationsRoute =
    removeRelationsEndpoint.toRoutes(ids => toRoute(service.removeRelations(ids)))

  def toRoute[A](fa: F[A]): F[Either[(StatusCode, ErrorInfo), A]] =
    fa.map(_.asRight[(StatusCode, ErrorInfo)]).handleError(err => handleError(err).asLeft[A])

  def handleError(err: Throwable): (StatusCode, ErrorInfo) = err match {
    case IncorrectEntitiesPerPage => (StatusCode.BadRequest, ErrorInfo(err.getLocalizedMessage))
    case IncorrectPageNumber      => (StatusCode.BadRequest, ErrorInfo(err.getLocalizedMessage))
    case _                        => (StatusCode.InternalServerError, ErrorInfo(err.getLocalizedMessage))
  }

  val route: HttpRoutes[F] = findEntityRoute
    .combineK(findEntitiesRoute)
    .combineK(findClosestEntityRelationsRoute)
    .combineK(findEntitiesByFilterRoute)
    .combineK(createEntityRoute)
    .combineK(updateEntityRoute)
    .combineK(createRelationRoute)
    .combineK(removeEntityRoute)
    .combineK(removeEntitiesRoute)
    .combineK(removeAllEntityRelationsRoute)
    .combineK(removeRelationRoute)
    .combineK(removeRelationsRoute)
}

object RestApi {
  val apiVersion = "v1"

  def apply[F[_]: Sync: ContextShift](service: Service[F]): RestApi[F] = new RestApi(service)
}
