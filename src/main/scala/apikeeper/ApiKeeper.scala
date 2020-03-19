package apikeeper

import apikeeper.datasource.{DataStorage, Migration, QueryRunner, Transactor}
import apikeeper.http.{HttpServer, RestApi, SwaggerApi}
import apikeeper.repository.KeeperRepository
import apikeeper.service.internal.{IdGenerator, IdGeneratorImpl}
import apikeeper.service.{KeeperService, Service}
import cats.Applicative
import distage.{GCMode, Injector, ModuleDef, TagK}
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Sync, Timer}
import org.neo4j.driver.Driver

object ApiKeeper extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    cfg <- Configuration.create[IO]
    _ <- Injector().produceF[IO](appModule[IO](cfg), GCMode.NoGC).use { locator =>
      val server = locator.get[HttpServer[IO]]
      val migrator = locator.get[Migration[IO]]

      migrator.migrate().flatMap(_ => server.run())
    }
  } yield ExitCode.Success

  def appModule[F[_]: TagK: ConcurrentEffect: ContextShift: Timer](configuration: Configuration) = {
    val driver: Resource[F, Driver] = DataStorage[F](configuration.neo4jSettings).connect()

    new ModuleDef {
      make[Driver].fromResource(driver)
      make[RestApi[F]]
      make[SwaggerApi[F]]
      make[HttpServer[F]]
      make[QueryRunner[F]]
      make[Transactor[F]]
      make[Migration[F]]
      make[KeeperRepository[F]]
      make[IdGenerator[F]].from[IdGeneratorImpl[F]]
      make[Service[F]].from[KeeperService[F]]

      addImplicit[Sync[F]]
      addImplicit[ContextShift[F]]
      addImplicit[Applicative[F]]
      addImplicit[Timer[F]]
      addImplicit[Bracket[F, Throwable]]
      addImplicit[ConcurrentEffect[F]]
    }
  }
}
