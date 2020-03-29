package apikeeper.http

import apikeeper.Configuration
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import org.http4s.server.staticcontent._
import cats.syntax.semigroupk._

class HttpServer[F[_]: ConcurrentEffect: ContextShift: Timer](api: RestApi[F], swaggerApi: SwaggerApi[F], configuration: Configuration) {
  def run(): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(configuration.serverSettings.port, configuration.serverSettings.host)
      .withHttpApp(
        Router(
          "/" -> api.route.combineK(swaggerApi.route)
//          "static" -> fileService(FileService.Config("./assets", blocker))
        ).orNotFound
      )
      .serve
      .compile
      .drain
}

object HttpServer {
  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](
    api: RestApi[F],
    swaggerApi: SwaggerApi[F],
    configuration: Configuration
  ): HttpServer[F] =
    new HttpServer(api, swaggerApi, configuration)
}
