package apikeeper.repository

import apikeeper.datasource.Transactor.{Task, Tx}
import apikeeper.model.{Api, Endpoint, Id, Service, Usage}
import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.option._
import cats.syntax.applicativeError._
import org.neo4j.driver.exceptions.NoSuchRecordException
import org.neo4j.driver.{Query, Values}
import org.pure4s.logger4s.LazyLogging
import org.pure4s.logger4s.cats.Logger

class ApiRepository[F[_]](
  transact: Task[F]
)(implicit F: Sync[F])
    extends Repository[Tx[F, *]]
    with LazyLogging {
  override def findApi(apiId: Id): Tx[F, Option[Api]] =
    for {
      _ <- Kleisli.liftF(Logger[F].info(s"Find Api by id: $apiId"))
      result <- transact(new Query("MATCH (self:Api) WHERE self.id = $id RETURN self", Values.parameters("id", apiId)))
        .flatMapF(result =>
          F.catchNonFatal(Api.fromRecord(result.single()).some).recover {
            case _: NoSuchRecordException => None
          }
        )
    } yield result

  override def createApi(api: Api): Tx[F, Api] = for {
    _ <- Kleisli.liftF(Logger[F].info(s"Save api: $api"))
    _ <- transact(new Query("CREATE (self:Api {id: $id})".stripMargin, Values.parameters(("id", api.id))))
  } yield api

  override def removeApi(apiId: Id): Tx[F, Unit] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def findService(serviceId: Id): Tx[F, Option[Service]] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result =>
      F.catchNonFatal(Service.fromRecord(result.single()).some).recover {
        case _: NoSuchRecordException => None
      }
    )

  override def createService(service: Service): Tx[F, Service] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def removeService(serviceId: Id): Tx[F, Unit] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def addEndpoint(apiId: Id, endpoint: Endpoint): Tx[F, Endpoint] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def removeEndpoint(apiId: Id, endpointId: Id): Tx[F, Unit] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def addUsage(sourceId: Id, targetId: Id, usage: Usage): Tx[F, Usage] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

  override def removeUsage(usageId: Id): Tx[F, Unit] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)
}

object ApiRepository {
  def apply[F[_]: Sync](transact: Task[F]): ApiRepository[F] = new ApiRepository(transact)
}
