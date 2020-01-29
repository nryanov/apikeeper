package apikeeper.service

import apikeeper.datasource.Transactor.Task
import apikeeper.model.{Api, Endpoint, Id, Usage, Service => MService}
import apikeeper.repository.ApiRepository
import cats.effect.Sync

class ApiService[F[_]: Sync](
  transact: Task[F],
  apiRepository: ApiRepository[F]
) extends Service[F] {
  override def findApi(apiId: Id): F[Option[Api]] = {
    ???
    ???
  }

  override def createApi(api: Api): F[Api] = ???

  override def removeApi(apiId: Id): F[Unit] = ???

  override def findService(serviceId: Id): F[Option[MService]] = ???

  override def createService(service: MService): F[MService] = ???

  override def removeService(serviceId: Id): F[Unit] = ???

  override def addEndpoint(apiId: Id, endpoint: Endpoint): F[Endpoint] = ???

  override def removeEndpoint(apiId: Id, endpointId: Id): F[Unit] = ???

  override def addUsage(sourceId: Id, targetId: Id, usage: Usage): F[Usage] = ???

  override def removeUsage(usageId: Id): F[Unit] = ???
}
