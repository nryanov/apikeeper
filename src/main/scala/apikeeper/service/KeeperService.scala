package apikeeper.service

import apikeeper.datasource.Transactor.Task
import apikeeper.repository.KeeperRepository
import cats.effect.Sync

class KeeperService[F[_]: Sync](
  transact: Task[F],
  apiRepository: KeeperRepository[F]
) extends Service[F] {}
