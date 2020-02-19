package apikeeper.http

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

class HttpServer[F[_]: ConcurrentEffect: ContextShift: Timer](api: Api[F]) {
  def run(): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(Router("/" -> api.helloWorldRoutes).orNotFound)
      .serve
      .compile
      .drain
}

object HttpServer {
  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](api: Api[F]): HttpServer[F] = new HttpServer(api)
}
