package apikeeper.repository

import apikeeper.{IOSpec, Neo4jSettings}
import apikeeper.datasource.{DataStorage, Transactor}
import apikeeper.model.{Entity, EntityType, Id, Relation}
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.neo4j.driver.Driver
import org.scalatest.BeforeAndAfterEach

class KeeperRepositorySpec extends IOSpec with TestContainerForAll with BeforeAndAfterEach {
  override val containerDef: Neo4jContainer.Def = Neo4jContainer.Def(dockerImageName = "neo4j:4.0.0")

  private var driver: Driver = _
  private var transactor: Transactor[F] = _
  private var apiRepository: KeeperRepository[F] = _
  private var finalizers: F[Unit] = _

  override def afterContainersStart(container: Neo4jContainer): Unit = {
    val dataStorage = DataStorage[F](Neo4jSettings(container.boltUrl, container.username, container.password))
      .connect()
      .allocated
      .unsafeRunSync()
    driver = dataStorage._1
    finalizers = dataStorage._2
    transactor = Transactor[F](driver)
    apiRepository = KeeperRepository[F](transactor.runner)
  }

  override def beforeContainersStop(containers: Neo4jContainer): Unit = finalizers.unsafeRunSync()

  override protected def afterEach(): Unit = driver.session().run("match (n) detach delete n")

  "keeper repository" should {
    "create entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        result <- transactor.transactSync(apiRepository.createEntity(entity))
      } yield assertResult(entity)(result)
    }

    "find entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        task = apiRepository.createEntity(entity).flatMap(entity => apiRepository.findEntity(entity.id))
        saved <- transactor.transactSync(task)
      } yield assert(saved.contains(entity))
    }

    "find entity definitions from first page" in runF {
      for {
        id1 <- fixedUUID.randomUUI()
        id2 <- fixedUUID.randomUUI()
        entity1 = Entity(Id(id1), EntityType.Service, "service")
        entity2 = Entity(Id(id2), EntityType.Service, "service")
        task = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transactor.transactSync(task)
        result <- transactor.transactSync(apiRepository.findEntities(1, 5))
      } yield assertResult(Seq(entity1, entity2))(result)
    }

    "remove entity definition" in runF {
      for {
        uuid <- fixedUUID.fixedUUID()
        entity = Entity(Id(uuid), EntityType.Service, "service")
        saveTask = apiRepository.createEntity(entity).flatMap(entity => apiRepository.findEntity(entity.id))
        saved <- transactor.transactSync(saveTask)
        deleteTask = apiRepository.removeEntity(entity.id)
        _ <- transactor.transactSync(deleteTask)
        found <- transactor.transactSync(apiRepository.findEntity(entity.id))
      } yield {
        assert(saved.contains(entity))
        assert(found.isEmpty)
      }
    }

    "create relation" in runF {
      for {
        id1 <- fixedUUID.randomUUI()
        id2 <- fixedUUID.randomUUI()
        entity1 = Entity(Id(id1), EntityType.Service, "service1")
        entity2 = Entity(Id(id2), EntityType.Service, "service2")
        saveTask = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transactor.transactSync(saveTask)
        relId <- fixedUUID.fixedUUID()
        relation = Relation(Id(relId))
        result <- transactor.transactSync(apiRepository.createRelation(entity1, entity2, relation))
      } yield assertResult(relation)(result)
    }

    "find closest entity relations" in runF {
      for {
        id1 <- fixedUUID.randomUUI()
        id2 <- fixedUUID.randomUUI()
        entity1 = Entity(Id(id1), EntityType.Service, "service1")
        entity2 = Entity(Id(id2), EntityType.Service, "service2")
        saveTask = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transactor.transactSync(saveTask)
        relId1 <- fixedUUID.randomUUI()
        relId2 <- fixedUUID.randomUUI()
        relation1 = Relation(Id(relId1))
        relation2 = Relation(Id(relId2))
        _ <- transactor.transactSync(apiRepository.createRelation(entity1, entity2, relation1))
        _ <- transactor.transactSync(apiRepository.createRelation(entity2, entity1, relation2))
        result <- transactor.transactSync(apiRepository.findClosestEntityRelations(entity1.id))
      } yield assertResult(Seq(relation2, relation1))(result)
    }

    "remove all entity relations" in runF {
      for {
        id1 <- fixedUUID.randomUUI()
        id2 <- fixedUUID.randomUUI()
        entity1 = Entity(Id(id1), EntityType.Service, "service1")
        entity2 = Entity(Id(id2), EntityType.Service, "service2")
        saveTask = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transactor.transactSync(saveTask)
        relId1 <- fixedUUID.randomUUI()
        relId2 <- fixedUUID.randomUUI()
        relation1 = Relation(Id(relId1))
        relation2 = Relation(Id(relId2))
        _ <- transactor.transactSync(apiRepository.createRelation(entity1, entity2, relation1))
        _ <- transactor.transactSync(apiRepository.createRelation(entity2, entity1, relation2))
        relations <- transactor.transactSync(apiRepository.findClosestEntityRelations(entity1.id))
        _ <- transactor.transactSync(apiRepository.removeAllEntityRelations(entity1.id))
        result <- transactor.transactSync(apiRepository.findClosestEntityRelations(entity1.id))
      } yield {
        assertResult(Seq(relation2, relation1))(relations)
        assert(result.isEmpty)
      }
    }

    "remove relation by id" in runF {
      for {
        id1 <- fixedUUID.randomUUI()
        id2 <- fixedUUID.randomUUI()
        entity1 = Entity(Id(id1), EntityType.Service, "service1")
        entity2 = Entity(Id(id2), EntityType.Service, "service2")
        saveTask = apiRepository.createEntity(entity1).flatMap(_ => apiRepository.createEntity(entity2))
        _ <- transactor.transactSync(saveTask)
        relId1 <- fixedUUID.randomUUI()
        relId2 <- fixedUUID.randomUUI()
        relation1 = Relation(Id(relId1))
        relation2 = Relation(Id(relId2))
        _ <- transactor.transactSync(apiRepository.createRelation(entity1, entity2, relation1))
        _ <- transactor.transactSync(apiRepository.createRelation(entity2, entity1, relation2))
        relations <- transactor.transactSync(apiRepository.findClosestEntityRelations(entity1.id))
        _ <- transactor.transactSync(apiRepository.removeRelation(relation1.id))
        result <- transactor.transactSync(apiRepository.findClosestEntityRelations(entity1.id))
      } yield {
        assertResult(Seq(relation2, relation1))(relations)
        assertResult(Seq(relation2))(result)
      }
    }
  }
}
