package apikeeper.http

import cats.effect.{ContextShift, Sync}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.openapi.circe.yaml._

class SwaggerApi[F[_]: Sync: ContextShift](restApi: RestApi[F]) {
  import RestApi._

  private val openApiDocs: OpenAPI = List(
    restApi.findEntityEndpoint,
    restApi.findEntitiesEndpoint,
    restApi.findClosestEntityRelationsEndpoint,
    restApi.createEntityEndpoint,
    restApi.createEntitiesEndpoint,
    restApi.createRelationEndpoint,
    restApi.createRelationsEndpoint,
    restApi.removeEntityEndpoint,
    restApi.removeEntitiesEndpoint,
    restApi.removeAllEntityRelationsEndpoint,
    restApi.removeRelationEndpoint,
    restApi.removeRelationsEndpoint
  ).toOpenAPI("ApiKeeper doc", apiVersion)

  val route = new SwaggerHttp4s(openApiDocs.toYaml).routes
}

object SwaggerApi {
  def apply[F[_]: Sync: ContextShift](restApi: RestApi[F]): SwaggerApi[F] = new SwaggerApi(restApi)
}
