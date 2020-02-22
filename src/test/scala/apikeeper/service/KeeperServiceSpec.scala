package apikeeper.service

import apikeeper.datasource.{DataStorage, Migration, QueryRunner, Transactor}
import apikeeper.{DISpec, Neo4jSettings}
import apikeeper.model.{Entity, EntityType, Id}
import apikeeper.repository.KeeperRepository
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import distage.{GCMode, Injector, ModuleDef}
import izumi.distage.model.Locator
import izumi.distage.model.definition.DIResource
import org.neo4j.driver.Driver
import org.scalatest.{BeforeAndAfterEach, EitherValues}

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
      make[KeeperService[F]]
      addImplicit[Sync[F]]
      addImplicit[ContextShift[F]]
      addImplicit[Timer[F]]
      addImplicit[Bracket[F, Throwable]]
      addImplicit[ConcurrentEffect[F]]
    }

  "keeper service" should {
    "create and find entity" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        id <- fixedUUID.randomUUI().map(Id(_))
        entity = Entity(id, EntityType.Service, "service")
        _ <- service.createEntity(entity)
        result <- service.findEntity(id)
      } yield {
        assert(result.contains(entity))
      }
    }

    "create and find entities" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        id1 <- fixedUUID.randomUUI().map(Id(_))
        id2 <- fixedUUID.randomUUI().map(Id(_))
        entity1 = Entity(id1, EntityType.Service, "service1")
        entity2 = Entity(id2, EntityType.Service, "service2")
        _ <- service.createEntities(Seq(entity1, entity2))
        result <- service.findEntities(1, 5)
      } yield {
        result mustBe Seq(entity1, entity2)
      }
    }

    "throw error due to duplicate entity" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        id <- fixedUUID.randomUUI().map(Id(_))
        entity = Entity(id, EntityType.Service, "service")
        _ <- service.createEntity(entity)
        result <- service.createEntity(entity).attempt
      } yield {
        result.left.value mustBe a[Exception]
      }
    }
  }
}
