package apikeeper.repository

import apikeeper.model.{Api, Endpoint, Id, Service, Usage}

trait Repository[F[_]] {
  def findApi(apiId: Id): F[Option[Api]]

  def createApi(api: Api): F[Api]

  def removeApi(apiId: Id): F[Unit]

  def findService(serviceId: Id): F[Option[Service]]

  def createService(service: Service): F[Service]

  def removeService(serviceId: Id): F[Unit]

  def addEndpoint(apiId: Id, endpoint: Endpoint): F[Endpoint]

  def removeEndpoint(apiId: Id, endpointId: Id): F[Unit]

  def addUsage(sourceId: Id, targetId: Id, usage: Usage): F[Usage]

  def removeUsage(usageId: Id): F[Unit]
}
