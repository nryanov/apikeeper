package apikeeper

import apikeeper.http.{Api, HttpServer}
import distage.{GCMode, Injector, ModuleDef, TagK}
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Sync, Timer}
import cats.syntax.functor._

object ApiKeeper extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    Injector().produceF[IO](appModule[IO], GCMode.NoGC).use(_.get[HttpServer[IO]].run().as(ExitCode.Success))

  def appModule[F[_]: TagK: ConcurrentEffect: ContextShift: Timer] = new ModuleDef {
    make[Api[F]]
    make[HttpServer[F]]
    addImplicit[Sync[F]]
    addImplicit[ContextShift[F]]
    addImplicit[Timer[F]]
    addImplicit[ConcurrentEffect[F]]
  }
}
