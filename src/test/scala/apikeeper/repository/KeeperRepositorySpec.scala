package apikeeper.repository

import cats.~>
import cats.syntax.option._
import org.neo4j.driver.Driver
import org.scalatest.BeforeAndAfterEach
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import apikeeper.datasource.Transactor.Tx
import apikeeper.{IOSpec, Neo4jSettings}
import apikeeper.datasource.{DataStorage, QueryRunner, Transactor}
import apikeeper.model.graph.{Branch, Leaf}
import apikeeper.model.{Entity, EntityType, Id, Relation, RelationType}

class KeeperRepositorySpec extends IOSpec with TestContainerForAll with BeforeAndAfterEach {
  override val containerDef: Neo4jContainer.Def = Neo4jContainer.Def(dockerImageName = "neo4j:4.0.0")

  private var driver: Driver = _
  private var apiRepository: KeeperRepository[F] = _
  private var finalizers: F[Unit] = _
  private var transact: ~>[Tx[F, *], F] = _

  override def afterContainersStart(container: Neo4jContainer): Unit = {
    val dataStorage =
      DataStorage[F](Neo4jSettings(container.boltUrl, container.username, container.password)).connect().allocated.unsafeRunSync()
    driver = dataStorage._1
    finalizers = dataStorage._2
    val transactor = Transactor[F](driver)
    val queryRunner = QueryRunner[F]()
    apiRepository = KeeperRepository[F](queryRunner)
    transact = transactor.transact()
  }

  override def beforeContainersStop(containers: Neo4jContainer): Unit = finalizers.unsafeRunSync()

  override protected def afterEach(): Unit = driver.session().run("match (n) detach delete n")

  "keeper repository" should {
    "update entity" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        createdEntity <- transact(apiRepository.createEntity(entity))
        updatedEntity = createdEntity.copy(name = "updatedService")
        _ <- transact(apiRepository.updateEntity(updatedEntity))
        result <- transact(apiRepository.findEntity(entity.id))
      } yield assert(result.contains(updatedEntity))
    }

    "create entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        result <- transact(apiRepository.createEntity(entity))
      } yield assertResult(entity)(result)
    }

    "find entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        task = apiRepository.createEntity(entity).flatMap(entity => apiRepository.findEntity(entity.id))
        saved <- transact(task)
      } yield assert(saved.contains(entity))
    }

    "find entity definitions from first page" in runF {
      for {
        id1 <- fixedUUID.randomUUID()
        id2 <- fixedUUID.randomUUID()
        entity1 = Entity(Id(id1), EntityType.Service, "service")
        entity2 = Entity(Id(id2), EntityType.Service, "service")
        task = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transact(task)
        result <- transact(apiRepository.findEntities(page = 1, countPerPage = 5))
      } yield assertResult(Seq(entity1, entity2))(result)
    }

    "find entity definitions by name pattern (1)" in runF {
      for {
        id1 <- fixedUUID.randomUUID()
        id2 <- fixedUUID.randomUUID()
        entity1 = Entity(Id(id1), EntityType.Storage, "storage")
        entity2 = Entity(Id(id2), EntityType.Service, "service")
        task = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transact(task)
        // will find first entity because limit will be equal to 1
        result <- transact(apiRepository.findEntitiesByNameLike(pattern = "s", limit = 1))
      } yield assertResult(Seq(entity1))(result)
    }

    "find entity definitions by name pattern (2)" in runF {
      for {
        id1 <- fixedUUID.randomUUID()
        id2 <- fixedUUID.randomUUID()
        entity1 = Entity(Id(id1), EntityType.Storage, "storage")
        entity2 = Entity(Id(id2), EntityType.Service, "service")
        task = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transact(task)
        result <- transact(apiRepository.findEntitiesByNameLike("ervic"))
      } yield assertResult(Seq(entity2))(result)
    }

    "remove entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        saveTask = apiRepository.createEntity(entity).flatMap(entity => apiRepository.findEntity(entity.id))
        saved <- transact(saveTask)
        deleteTask = apiRepository.removeEntity(entity.id)
        _ <- transact(deleteTask)
        found <- transact(apiRepository.findEntity(entity.id))
      } yield {
        assert(saved.contains(entity))
        assert(found.isEmpty)
      }
    }

    "create relation" in runF {
      for {
        id1 <- fixedUUID.randomUUID().map(Id(_))
        id2 <- fixedUUID.randomUUID().map(Id(_))
        entity1 = Entity(id1, EntityType.Service, "service1")
        entity2 = Entity(id2, EntityType.Service, "service2")
        _ <- transact(apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2)))
        relId <- fixedUUID.fixedUUID().map(Id(_))
        relation = Relation(relId, RelationType.In)
        result <- transact(apiRepository.createRelation(Branch(id1, relation, id2)))
      } yield assertResult(relation)(result)
    }

    "find closest entity relations" in runF {
      for {
        id1 <- fixedUUID.randomUUID().map(Id(_))
        id2 <- fixedUUID.randomUUID().map(Id(_))
        entity1 = Entity(id1, EntityType.Service, "service1")
        entity2 = Entity(id2, EntityType.Service, "service2")
        _ <- transact(apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2)))
        relId1 <- fixedUUID.randomUUID()
        relId2 <- fixedUUID.randomUUID()
        relation1 = Relation(Id(relId1), RelationType.In)
        relation2 = Relation(Id(relId2), RelationType.Out)
        _ <- transact(apiRepository.createRelation(Branch(id1, relation1, id2)))
        _ <- transact(apiRepository.createRelation(Branch(id1, relation2, id2)))
        result <- transact(apiRepository.findClosestEntityRelations(entity1.id))
      } yield assertResult(Seq(Leaf(entity2, relation2), Leaf(entity2, relation1)))(result)
    }

    "remove all entity relations" in runF {
      for {
        id1 <- fixedUUID.randomUUID().map(Id(_))
        id2 <- fixedUUID.randomUUID().map(Id(_))
        entity1 = Entity(id1, EntityType.Service, "service1")
        entity2 = Entity(id2, EntityType.Service, "service2")
        _ <- transact(apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2)))
        relId1 <- fixedUUID.randomUUID().map(Id(_))
        relId2 <- fixedUUID.randomUUID().map(Id(_))
        relation1 = Relation(relId1, RelationType.In)
        relation2 = Relation(relId2, RelationType.Out)
        _ <- transact(apiRepository.createRelation(Branch(id1, relation1, id2)))
        _ <- transact(apiRepository.createRelation(Branch(id1, relation2, id2)))
        relations <- transact(apiRepository.findClosestEntityRelations(entity1.id))
        _ <- transact(apiRepository.removeAllEntityRelations(entity1.id))
        result <- transact(apiRepository.findClosestEntityRelations(entity1.id))
      } yield {
        assertResult(Seq(Leaf(entity2, relation2), Leaf(entity2, relation1)))(relations)
        assert(result.isEmpty)
      }
    }

    "remove relation by id" in runF {
      for {
        id1 <- fixedUUID.randomUUID().map(Id(_))
        id2 <- fixedUUID.randomUUID().map(Id(_))
        entity1 = Entity(id1, EntityType.Service, "service1")
        entity2 = Entity(id2, EntityType.Service, "service2")
        _ <- transact(apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2)))
        relId1 <- fixedUUID.randomUUID().map(Id(_))
        relId2 <- fixedUUID.randomUUID().map(Id(_))
        relation1 = Relation(relId1, RelationType.In)
        relation2 = Relation(relId2, RelationType.Out)
        _ <- transact(apiRepository.createRelation(Branch(id1, relation1, id2)))
        _ <- transact(apiRepository.createRelation(Branch(id1, relation2, id2)))
        relations <- transact(apiRepository.findClosestEntityRelations(entity1.id))
        _ <- transact(apiRepository.removeRelation(relation1.id))
        result <- transact(apiRepository.findClosestEntityRelations(entity1.id))
      } yield {
        assertResult(Seq(Leaf(entity2, relation2), Leaf(entity2, relation1)))(relations)
        assertResult(Seq(Leaf(entity2, relation2)))(result)
      }
    }
  }
}
