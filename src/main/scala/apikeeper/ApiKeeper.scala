package apikeeper

import java.util.concurrent.Executors

import apikeeper.datasource.{DataStorage, Migration, QueryRunner, Transactor}
import apikeeper.http.{HttpServer, RestApi, SwaggerApi}
import apikeeper.repository.KeeperRepository
import apikeeper.service.internal.{IdGenerator, IdGeneratorImpl}
import apikeeper.service.{KeeperService, Service}
import cats.Applicative
import distage.{GCMode, Injector, ModuleDef, TagK}
import cats.effect.{Blocker, Bracket, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Sync, Timer}
import org.neo4j.driver.Driver
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

import scala.concurrent.ExecutionContext

object ApiKeeper extends IOApp with LazyLogging {
  override def run(args: List[String]): IO[ExitCode] = for {
    cfg <- Configuration.create[IO]
    result <- Injector()
      .produceF[IO](appModule[IO](cfg), GCMode.NoGC)
      .use { locator =>
        val server = locator.get[HttpServer[IO]]
        val migrator = locator.get[Migration[IO]]

        migrator.migrate().flatMap(_ => server.run())
      }
      .map(_ => ExitCode.Success)
      .handleErrorWith(error => Logger[IO].error(error.getLocalizedMessage).map(_ => ExitCode.Error))

  } yield result

  def appModule[F[_]: TagK: ConcurrentEffect: ContextShift: Timer](configuration: Configuration) = {
    val driver: Resource[F, Driver] =
      DataStorage[F](configuration.neo4jSettings).connect().evalTap(driver => Sync[F].delay(driver.verifyConnectivity()))

    new ModuleDef {
      make[Blocker].named("transactionBlocker").fromResource(Blocker[F])
      make[Blocker]
        .named("staticFilesBlocker")
        .from(Blocker.liftExecutionContext(ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))))

      addImplicit[Sync[F]]
      addImplicit[ContextShift[F]]
      addImplicit[Applicative[F]]
      addImplicit[Timer[F]]
      addImplicit[Bracket[F, Throwable]]
      addImplicit[ConcurrentEffect[F]]

      make[Configuration].from(configuration)
      make[Driver].fromResource(driver)
      make[QueryRunner[F]]
      make[Transactor[F]]
      make[KeeperRepository[F]]
      make[IdGenerator[F]].from[IdGeneratorImpl[F]]
      make[Service[F]].from[KeeperService[F]]
      make[RestApi[F]]
      make[SwaggerApi[F]]
      make[HttpServer[F]]
      make[Migration[F]]
    }
  }
}
