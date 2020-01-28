package apikeeper.repository

import apikeeper.datasource.DataStorage.{Transact, Tx}
import apikeeper.model.{Api, Endpoint, Id, Service, Usage}
import cats.effect.Sync
import cats.syntax.option._
import cats.syntax.applicativeError._
import org.neo4j.driver.exceptions.NoSuchRecordException
import org.neo4j.driver.{Query, Values}

class ApiRepository[F[_]](
  transact: Transact[F]
)(implicit F: Sync[F])
    extends Repository[Tx[F, *]] {
  override def findApi(apiId: Id): Tx[F, Option[Api]] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result =>
      F.catchNonFatal(Api.fromRecord(result.single()).some).recover {
        case _: NoSuchRecordException => None
      }
    )

  override def createApi(api: Api): Tx[F, Api] =
    transact(new Query("", Values.EmptyMap)).flatMapF(result => ???)

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

object ApiRepository {}
