package apikeeper.service

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import org.neo4j.driver.Driver
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import distage.{GCMode, Injector, ModuleDef}
import izumi.distage.model.Locator
import izumi.distage.model.definition.DIResource
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import apikeeper.datasource.{DataStorage, Migration, QueryRunner, Transactor}
import apikeeper.{DISpec, FixedUUID, Neo4jSettings}
import apikeeper.model.{EntityDef, EntityType}
import apikeeper.repository.KeeperRepository
import apikeeper.service.internal.IdGenerator

class KeeperServiceSpec extends DISpec with TestContainerForAll with BeforeAndAfterEach with EitherValues {
  override val containerDef: Neo4jContainer.Def = Neo4jContainer.Def(dockerImageName = "neo4j:4.0.0")

  var injector: DIResource.DIResourceBase[F, Locator] = _

  override def afterContainersStart(container: Neo4jContainer): Unit = {
    val driver: Resource[F, Driver] = DataStorage[F](Neo4jSettings(container.boltUrl, container.username, container.password)).connect()

    injector = Injector().produceF[F](testModule(driver), GCMode.NoGC)
  }

  def testModule(driver: Resource[F, Driver]) =
    new ModuleDef {
      make[Driver].fromResource(driver)
      make[QueryRunner[F]]
      make[Transactor[F]]
      make[KeeperRepository[F]]
      make[Migration[F]]
      make[IdGenerator[F]].from[FixedUUID[F]]
      make[KeeperService[F]]
      addImplicit[Sync[F]]
      addImplicit[ContextShift[F]]
      addImplicit[Timer[F]]
      addImplicit[Applicative[F]]
      addImplicit[Bracket[F, Throwable]]
      addImplicit[ConcurrentEffect[F]]
    }

  "keeper service" should {
    "create and find entity" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef <- EntityDef(EntityType.Service, "service").pure
        entity <- service.createEntity(entityDef)
        result <- service.findEntity(entity.id)
      } yield {
        assert(result.contains(entity))
      }
    }

    "create and find entities" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Service, "service1").pure
        entityDef2 <- EntityDef(EntityType.Service, "service2").pure
        entities <- service.createEntities(Seq(entityDef1, entityDef2))
        result <- service.findEntities(1, 5)
      } yield {
        result mustBe entities
      }
    }
  }
}
