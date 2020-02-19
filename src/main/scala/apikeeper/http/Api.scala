package apikeeper.http

import cats.effect.{ContextShift, Sync}
import cats.syntax.applicative._
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

class Api[F[_]: ContextShift](implicit F: Sync[F]) {
  import Api._

  private val baseEndpoint: Endpoint[Unit, (StatusCode, ErrorInfo), Unit, Nothing] =
    endpoint.in(apiVersion).errorOut(statusCode.and(jsonBody[ErrorInfo]))

  val helloWorld =
    baseEndpoint.get.in("hello").in(query[String]("name")).out(stringBody)

  val helloWorldRoutes: HttpRoutes[F] =
    helloWorld.toRoutes(name => F.pure(s"Hello, $name!").map(_.asRight[(StatusCode, ErrorInfo)]))
}

object Api {
  private val apiVersion = "v1"

  def apply[F[_]: Sync: ContextShift](): Api[F] = new Api()
}
