package apikeeper.http

import apikeeper.model.{Entity, EntityDef, Id, Relation}
import apikeeper.model.graph.{Branch, Leaf}
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
    endpoint.in(apiVersion).errorOut(statusCode.and(jsonBody[ErrorInfo]))

  val findEntityEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Option[Entity], Nothing] =
    baseEndpoint.get.in("entity").in(query[Id]("id")).out(jsonBody[Option[Entity]])

  val findEntityRoute = findEntityEndpoint.toRoutes(id => toRoute(service.findEntity(id)))

  val findEntitiesEndpoint: Endpoint[(Int, Option[Int]), (StatusCode, ErrorInfo), Seq[Entity], Nothing] =
    baseEndpoint.get.in("entity").in(query[Int]("page")).in(query[Option[Int]]("entries")).out(jsonBody[Seq[Entity]])

  val findEntitiesRoute =
    findEntitiesEndpoint.toRoutes {
      case (page, count) => toRoute(service.findEntities(page, count.getOrElse(10)))
    }

  val findEntitiesByNameEndpoint: Endpoint[(String, Option[Int]), (StatusCode, ErrorInfo), Seq[Entity], Nothing] =
    baseEndpoint.get.in("entity").in(query[String]("name")).in(query[Option[Int]]("entries")).out(jsonBody[Seq[Entity]])

  val findEntitiesByNameRoute =
    findEntitiesByNameEndpoint.toRoutes {
      case (pattern, count) => toRoute(service.findEntitiesByNameLike(pattern, count))
    }

  val findClosestEntityRelationsEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Seq[Leaf], Nothing] =
    baseEndpoint.get.in("entity" / "relation").in(query[Id]("id")).out(jsonBody[Seq[Leaf]])

  val findClosestEntityRelationsRoute =
    findClosestEntityRelationsEndpoint.toRoutes(id => toRoute(service.findClosestEntityRelations(id)))

  val createEntityEndpoint: Endpoint[EntityDef, (StatusCode, ErrorInfo), Entity, Nothing] =
    baseEndpoint.post.in("entity").in(jsonBody[EntityDef]).out(jsonBody[Entity])

  val createEntityRoute =
    createEntityEndpoint.toRoutes(entityDef => toRoute(service.createEntity(entityDef)))

  val createEntitiesEndpoint: Endpoint[Seq[EntityDef], (StatusCode, ErrorInfo), Seq[Entity], Nothing] =
    baseEndpoint.post.in("entity").in(jsonBody[Seq[EntityDef]]).out(jsonBody[Seq[Entity]])

  val createEntitiesRoute =
    createEntitiesEndpoint.toRoutes(entityDefs => toRoute(service.createEntities(entityDefs)))

  val createRelationEndpoint: Endpoint[Branch, (StatusCode, ErrorInfo), Relation, Nothing] =
    baseEndpoint.post.in("relation").in(jsonBody[Branch]).out(jsonBody[Relation])

  val createRelationRoute =
    createRelationEndpoint.toRoutes(branch => toRoute(service.createRelation(branch)))

  val createRelationsEndpoint: Endpoint[Seq[Branch], (StatusCode, ErrorInfo), Seq[Relation], Nothing] =
    baseEndpoint.post.in("relation").in(jsonBody[Seq[Branch]]).out(jsonBody[Seq[Relation]])

  val createRelationsRoute =
    createRelationsEndpoint.toRoutes(branches => toRoute(service.createRelations(branches)))

  val removeEntityEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity").in(jsonBody[Id])

  val removeEntityRoute =
    removeEntityEndpoint.toRoutes(id => toRoute(service.removeEntity(id)))

  val removeEntitiesEndpoint: Endpoint[Seq[Id], (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity").in(jsonBody[Seq[Id]])

  val removeEntitiesRoute =
    removeEntitiesEndpoint.toRoutes(ids => toRoute(service.removeEntities(ids)))

  val removeAllEntityRelationsEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("entity" / "relation").in(jsonBody[Id])

  val removeAllEntityRelationsRoute =
    removeAllEntityRelationsEndpoint.toRoutes(id => toRoute(service.removeAllEntityRelations(id)))

  val removeRelationEndpoint: Endpoint[Id, (StatusCode, ErrorInfo), Unit, Nothing] =
    baseEndpoint.delete.in("relation").in(jsonBody[Id])

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
    .combineK(findEntitiesByNameRoute)
    .combineK(createEntityRoute)
    .combineK(createEntitiesRoute)
    .combineK(createRelationRoute)
    .combineK(createRelationsRoute)
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
