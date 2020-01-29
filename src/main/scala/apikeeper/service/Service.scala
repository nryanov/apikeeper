package apikeeper.service

import apikeeper.model.{Api, Endpoint, Id, Service => MService, Usage}

trait Service[F[_]] {
  def findApi(apiId: Id): F[Option[Api]]

  def createApi(api: Api): F[Api]

  def removeApi(apiId: Id): F[Unit]

  def findService(serviceId: Id): F[Option[MService]]

  def createService(service: MService): F[MService]

  def removeService(serviceId: Id): F[Unit]

  def addEndpoint(apiId: Id, endpoint: Endpoint): F[Endpoint]

  def removeEndpoint(apiId: Id, endpointId: Id): F[Unit]

  def addUsage(sourceId: Id, targetId: Id, usage: Usage): F[Usage]

  def removeUsage(usageId: Id): F[Unit]
}
