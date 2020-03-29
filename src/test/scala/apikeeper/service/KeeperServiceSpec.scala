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
import apikeeper.model.graph.BranchDef
import apikeeper.{Configuration, DISpec, FixedUUID, Neo4jSettings}
import apikeeper.model.{EntityDef, EntityType, RelationDef, RelationType}
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
      make[Configuration].fromEffect(Configuration.create[F])
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

    "update entity" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef <- EntityDef(EntityType.Service, "service").pure
        createdEntity <- service.createEntity(entityDef)
        updatedEntity = createdEntity.copy(name = "updatedService")
        _ <- service.updateEntity(updatedEntity)
        result <- service.findEntity(createdEntity.id)
      } yield assert(result.contains(updatedEntity))
    }

    "find all entities" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Service, "service1").pure
        entityDef2 <- EntityDef(EntityType.Service, "service2").pure
        response <- service.createEntities(Seq(entityDef1, entityDef2))
        result <- service.findAllEntities()
      } yield assertResult(response)(result)
    }

    "find all entities by type" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entity <- service.createEntity(entityDef1)
        result <- service.findEntitiesByType(EntityType.Storage)
      } yield assertResult(Seq(entity))(result)
    }

    "find entity definitions by name pattern (1)" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entityDef2 <- EntityDef(EntityType.Service, "service").pure
        entity1 <- service.createEntity(entityDef1)
        _ <- service.createEntity(entityDef2)
        result <- service.findEntitiesByNameLike("tor", 1)
      } yield assertResult(Seq(entity1))(result)
    }

    "find entity definitions by name pattern (2)" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entityDef2 <- EntityDef(EntityType.Service, "service").pure
        _ <- service.createEntity(entityDef1)
        entity2 <- service.createEntity(entityDef2)
        result <- service.findEntitiesByNameLike("ervic", 1)
      } yield assertResult(Seq(entity2))(result)
    }

    "remove entity definition" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entity <- service.createEntity(entityDef1)
        result <- service.findAllEntities()
        _ <- service.removeEntity(entity.id)
        resultAfterDeletion <- service.findAllEntities()
      } yield {
        assertResult(Seq(entity))(result)
        assert(resultAfterDeletion.isEmpty)
      }
    }

    "create relation" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entityDef2 <- EntityDef(EntityType.Service, "service").pure
        entity1 <- service.createEntity(entityDef1)
        entity2 <- service.createEntity(entityDef2)
        relationDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        _ <- service.createRelation(relationDef)
        result <- service.findClosestEntityRelations(entity1.id)
      } yield assert(result.size == 1)
    }

    "remove all entity relations" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entityDef2 <- EntityDef(EntityType.Service, "service").pure
        entity1 <- service.createEntity(entityDef1)
        entity2 <- service.createEntity(entityDef2)
        relationDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        _ <- service.createRelation(relationDef)
        relations <- service.findClosestEntityRelations(entity1.id)
        _ <- service.removeAllEntityRelations(entity1.id)
        relationsAfterDeletion <- service.findClosestEntityRelations(entity1.id)
      } yield {
        assert(relations.size == 1)
        assert(relationsAfterDeletion.isEmpty)
      }
    }

    "remove relation by id" in runDI { locator =>
      val service = locator.get[KeeperService[F]]
      for {
        entityDef1 <- EntityDef(EntityType.Storage, "storage").pure
        entityDef2 <- EntityDef(EntityType.Service, "service").pure
        entity1 <- service.createEntity(entityDef1)
        entity2 <- service.createEntity(entityDef2)
        relationDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        relation <- service.createRelation(relationDef)
        relations <- service.findClosestEntityRelations(entity1.id)
        _ <- service.removeRelation(relation.id)
        relationsAfterDeletion <- service.findClosestEntityRelations(entity1.id)
      } yield {
        assert(relations.size == 1)
        assert(relationsAfterDeletion.isEmpty)
      }
    }
  }
}
