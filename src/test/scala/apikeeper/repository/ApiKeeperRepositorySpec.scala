package apikeeper.repository

import apikeeper.{IOSpec, Neo4jSettings}
import apikeeper.datasource.{DataStorage, Transactor}
import apikeeper.model.{Api, Id}
import com.dimafeng.testcontainers.Neo4jContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.neo4j.driver.Driver

class ApiKeeperRepositorySpec extends IOSpec with TestContainerForAll {
  override val containerDef: Neo4jContainer.Def = Neo4jContainer.Def(dockerImageName = "neo4j:4.0.0")

  private var driver: Driver = _
  private var transactor: Transactor[F] = _
  private var apiRepository: ApiRepository[F] = _
  private var finalizers: F[Unit] = _

  override def afterContainersStart(container: Neo4jContainer): Unit = {
    val dataStorage = DataStorage[F](Neo4jSettings(container.boltUrl)).connect().allocated.unsafeRunSync()
    driver = dataStorage._1
    finalizers = dataStorage._2
    transactor = Transactor[F](driver)
    apiRepository = ApiRepository[F](transactor.runner)
  }

  override def beforeContainersStop(containers: Neo4jContainer): Unit = finalizers.unsafeRunSync()

  "api repository" should {
    "save api definition" in runF {
      fixedUUID.fixedUUID().flatMap { uuid =>
        val api = Api(Id(uuid))
        transactor.transactSync(apiRepository.createApi(api))
      }

      for {
        uuid <- fixedUUID.fixedUUID()
        api = Api(Id(uuid))
        result <- transactor.transactSync(apiRepository.createApi(api))
      } yield assertResult(api)(result)
    }
  }
}
