package apikeeper.http

import cats.Applicative
import cats.syntax.show._
import cats.syntax.option._
import cats.data.OptionT
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, IO, Resource, Sync, Timer}
import apikeeper.datasource.{DataStorage, Migration, QueryRunner, Transactor}
import apikeeper.http._
import apikeeper.http.internal.{EntityTypeFilter, Filter, NameFilter}
import apikeeper.model._
import apikeeper.model.graph.{BranchDef, Leaf}
import apikeeper.repository.KeeperRepository
import apikeeper.service.{KeeperService, Service}
import apikeeper.service.internal.IdGenerator
import apikeeper.{Configuration, DISpec, FixedUUID, Neo4jSettings}
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import distage.{GCMode, Injector, ModuleDef}
import izumi.distage.model.Locator
import izumi.distage.model.definition.DIResource
import org.neo4j.driver.Driver
import org.http4s.implicits._
import org.http4s.{EntityDecoder, Method, Request, Response, Status, Uri}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._

import scala.util.control.NoStackTrace
import org.scalatest.{Assertion, BeforeAndAfterEach, EitherValues}

class RestApiSpec extends DISpec with TestContainerForAll with BeforeAndAfterEach with EitherValues {
  override val containerDef: Neo4jContainer.Def = Neo4jContainer.Def(dockerImageName = "neo4j:4.0.0")

  var injector: DIResource.DIResourceBase[IO, Locator] = _

  override def afterContainersStart(container: Neo4jContainer): Unit = {
    val driver: Resource[IO, Driver] = DataStorage[IO](Neo4jSettings(container.boltUrl, container.username, container.password)).connect()

    injector = Injector().produceF[IO](testModule(driver), GCMode.NoGC)
  }

  def testModule(driver: Resource[IO, Driver]) =
    new ModuleDef {
      make[Driver].fromResource(driver)
      make[Configuration].fromEffect(Configuration.create[IO])
      make[QueryRunner[IO]]
      make[Transactor[IO]]
      make[KeeperRepository[IO]]
      make[Migration[IO]]
      make[IdGenerator[IO]].from[FixedUUID[IO]]
      make[Service[IO]].from[KeeperService[IO]]
      make[RestApi[IO]]
      addImplicit[Sync[IO]]
      addImplicit[ContextShift[IO]]
      addImplicit[Timer[IO]]
      addImplicit[Applicative[IO]]
      addImplicit[Bracket[IO, Throwable]]
      addImplicit[ConcurrentEffect[IO]]
    }

  def run(response: OptionT[IO, Response[IO]]): IO[Response[IO]] =
    response.value.flatMap(_.liftTo[IO](EmptyResponseError))

  def checkPredicate[A](actualResp: Response[IO], expectedStatus: Status, predicate: A => Boolean)(
    implicit ev: EntityDecoder[IO, A]
  ): Assertion = {
    assertResult(expectedStatus)(actualResp.status)
    assert(predicate(actualResp.as[A].unsafeRunSync))
  }

  def checkStatus[A](actualResp: Response[IO], expectedStatus: Status)(
    implicit ev: EntityDecoder[IO, A]
  ): Assertion =
    assertResult(expectedStatus)(actualResp.status)

  def check[A](actualResp: Response[IO], expectedStatus: Status, expectedBody: A)(
    implicit ev: EntityDecoder[IO, A]
  ): Assertion = {
    assertResult(expectedStatus)(actualResp.status)
    assertResult(expectedBody)(actualResp.as[A].unsafeRunSync)
  }

  "rest api" should {
    "findEntity" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        entity <- service.createEntity(entityDef)
        response <- run(rest.run(Request[IO](method = Method.GET, uri = Uri(path = s"/v1/entity/${entity.id.show}"))))
      } yield {
        checkPredicate[Option[Entity]](response, Status.Ok, _.contains(entity))
      }
    }

    "removeEntity" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        entity <- service.createEntity(entityDef)
        response <- run(rest.run(Request[IO](method = Method.DELETE, uri = Uri(path = s"/v1/entity/${entity.id.show}"))))
        result <- service.findEntity(entity.id)
      } yield {
        checkStatus[Option[Entity]](response, Status.Ok)
        assert(result.isEmpty)
      }
    }

    "removeEntities" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef1 = EntityDef(EntityType.Service, "service1")
      val entityDef2 = EntityDef(EntityType.Service, "service2")

      for {
        entity1 <- service.createEntity(entityDef1)
        entity2 <- service.createEntity(entityDef2)
        response <- run(rest.run(Request[IO](method = Method.DELETE, uri = uri"/v1/entity/").withEntity(Seq(entity1.id, entity2.id))))
        result <- service.findEntities(1, 10)
      } yield {
        checkStatus[Seq[Entity]](response, Status.Ok)
        assert(result.isEmpty)
      }
    }

    "findEntity - None" in runDI { locator =>
      val rest = locator.get[RestApi[IO]].route

      for {
        id <- fixedUUID.next()
        response <- run(rest.run(Request[IO](method = Method.GET, uri = Uri(path = s"/v1/entity/${id.show}"))))
      } yield {
        checkStatus[Option[Entity]](response, Status.Ok)
      }
    }

    "createEntity" in runDI { locator =>
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        response <- run(
          rest.run(Request[IO](method = Method.POST, uri = uri"/v1/entity/").withEntity(entityDef))
        )
      } yield {
        checkStatus[Entity](response, Status.Ok)
      }
    }

    "updateEntity" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        entity <- service.createEntity(entityDef)
        updatedEntity = entity.copy(name = "updatedService")
        response <- run(
          rest.run(Request[IO](method = Method.PUT, uri = uri"/v1/entity/").withEntity(updatedEntity))
        )
        result <- service.findEntity(entity.id)
      } yield {
        checkStatus[Entity](response, Status.Ok)
        assert(result.contains(updatedEntity))
      }
    }

    "findEntities" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        entity <- service.createEntity(entityDef)
        response <- run(
          rest.run(Request[IO](method = Method.GET, uri = uri"/v1/entity/".withQueryParam("page", 1).withQueryParam("entries", 1)))
        )
      } yield {
        check[Seq[Entity]](response, Status.Ok, Seq(entity))
      }
    }

    "findAllEntities" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")

      for {
        entity <- service.createEntity(entityDef)
        response <- run(rest.run(Request[IO](method = Method.GET, uri = uri"/v1/entity/")))
      } yield {
        check[Seq[Entity]](response, Status.Ok, Seq(entity))
      }
    }

    "findEntitiesByName" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      val filter: Filter = NameFilter("tor", 1)

      for {
        _ <- service.createEntity(entityDef)
        entity <- service.createEntity(anotherDef)
        response <- run(rest.run(Request[IO](method = Method.GET, uri = uri"/v1/entity/filter").withEntity(filter)))
      } yield {
        check[Seq[Entity]](response, Status.Ok, Seq(entity))
      }
    }

    "findEntitiesByType" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      val filter: Filter = EntityTypeFilter(EntityType.Service)

      for {
        entity <- service.createEntity(entityDef)
        _ <- service.createEntity(anotherDef)
        response <- run(rest.run(Request[IO](method = Method.GET, uri = uri"/v1/entity/filter").withEntity(filter)))
      } yield {
        check[Seq[Entity]](response, Status.Ok, Seq(entity))
      }
    }

    "findClosestEntityRelations" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      for {
        entity1 <- service.createEntity(entityDef)
        entity2 <- service.createEntity(anotherDef)
        branchDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        relation <- service.createRelation(branchDef)
        response <- run(rest.run(Request[IO](method = Method.GET, uri = Uri(path = s"/v1/entity/${entity1.id.show}/relation"))))
      } yield {
        check[Seq[Leaf]](response, Status.Ok, Seq(Leaf(entity2, relation)))
      }
    }

    "removeAllEntityRelations" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      for {
        entity1 <- service.createEntity(entityDef)
        entity2 <- service.createEntity(anotherDef)
        branchDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        _ <- service.createRelation(branchDef)
        response <- run(
          rest.run(Request[IO](method = Method.DELETE, uri = Uri(path = s"/v1/entity/${entity1.id.show}/relation")))
        )
        result <- service.findClosestEntityRelations(entity1.id)
      } yield {
        assert(result.isEmpty)
        checkStatus[Seq[Leaf]](response, Status.Ok)
      }
    }

    "removeRelation" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      for {
        entity1 <- service.createEntity(entityDef)
        entity2 <- service.createEntity(anotherDef)
        branchDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        relation <- service.createRelation(branchDef)
        response <- run(rest.run(Request[IO](method = Method.DELETE, uri = Uri(path = s"/v1/relation/${relation.id.show}"))))
        result <- service.findClosestEntityRelations(entity1.id)
      } yield {
        assert(result.isEmpty)
        checkStatus[Seq[Leaf]](response, Status.Ok)
      }
    }

    "createRelation" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      for {
        entity1 <- service.createEntity(entityDef)
        entity2 <- service.createEntity(anotherDef)
        branchDef = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        response <- run(rest.run(Request[IO](method = Method.POST, uri = uri"/v1/relation/").withEntity(branchDef)))
      } yield {
        checkStatus[Relation](response, Status.Ok)
      }
    }

    "removeRelations" in runDI { locator =>
      val service = locator.get[Service[IO]]
      val rest = locator.get[RestApi[IO]].route

      val entityDef = EntityDef(EntityType.Service, "service")
      val anotherDef = EntityDef(EntityType.Storage, "storage")

      for {
        entity1 <- service.createEntity(entityDef)
        entity2 <- service.createEntity(anotherDef)
        branchDef1 = BranchDef(entity1.id, RelationDef(RelationType.Upstream), entity2.id)
        branchDef2 = BranchDef(entity1.id, RelationDef(RelationType.Downstream), entity2.id)
        relation1 <- service.createRelation(branchDef1)
        relation2 <- service.createRelation(branchDef2)
        response <- run(rest.run(Request[IO](method = Method.DELETE, uri = uri"/v1/relation/").withEntity(Seq(relation1.id, relation2.id))))
        result <- service.findClosestEntityRelations(entity1.id)
      } yield {
        assert(result.isEmpty)
        checkStatus[Seq[Relation]](response, Status.Ok)
      }
    }
  }

  case object EmptyResponseError extends RuntimeException("Empty response") with NoStackTrace
}
