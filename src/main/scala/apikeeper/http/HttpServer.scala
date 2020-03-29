package apikeeper.http

import apikeeper.Configuration
import cats.syntax.semigroupk._
import cats.syntax.option._
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import distage.Id

class HttpServer[F[_]: ConcurrentEffect: ContextShift: Timer](
  api: RestApi[F],
  swaggerApi: SwaggerApi[F],
  configuration: Configuration,
  blocker: Blocker @Id("staticFilesBlocker")
) extends Http4sDsl[F] {
  def run(): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(configuration.serverSettings.port, configuration.serverSettings.host)
      .withHttpApp(
        Router(
          "/" -> api.route.combineK(swaggerApi.route).combineK(staticFiles())
        ).orNotFound
      )
      .serve
      .compile
      .drain

  private def staticFiles(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> Root / "index" => StaticFile.fromResource[F]("WEB/index.html", blocker, req.some).getOrElseF(NotFound())
      case req @ GET -> path           => static(path.toString, req)
    }

  def static(file: String, request: Request[F]): F[Response[F]] =
    StaticFile.fromResource("WEB/" + file, blocker, request.some).getOrElseF(NotFound())
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
