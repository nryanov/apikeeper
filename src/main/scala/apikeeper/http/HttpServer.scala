package apikeeper.http

import apikeeper.Configuration
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import org.http4s.server.staticcontent._
import cats.syntax.semigroupk._
import distage.Id

class HttpServer[F[_]: ConcurrentEffect: ContextShift: Timer](
  api: RestApi[F],
  swaggerApi: SwaggerApi[F],
  configuration: Configuration,
  blocker: Blocker @Id("staticFilesBlocker")
) {
  def run(): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(configuration.serverSettings.port, configuration.serverSettings.host)
      .withHttpApp(
        Router(
          "/" -> api.route.combineK(swaggerApi.route),
          "static" -> fileService(FileService.Config("./assets", blocker))
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
    configuration: Configuration,
    blocker: Blocker
  ): HttpServer[F] =
    new HttpServer(api, swaggerApi, configuration, blocker)
}
