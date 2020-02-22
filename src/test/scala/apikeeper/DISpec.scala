package apikeeper

import apikeeper.datasource.Migration
import cats.data.OptionT
import cats.syntax.apply._
import izumi.distage.model.Locator
import izumi.distage.model.definition.DIResource
import org.scalatest.Assertion

trait DISpec extends IOSpec {
  def injector: DIResource.DIResourceBase[F, Locator]

  def runDI(fa: Locator => F[Assertion]): Assertion = injector.use { locator =>
    val migrator = locator.find[Migration[F]]

    OptionT
      .fromOption[F](migrator)
      .flatMap(migrator => OptionT.liftF(migrator.truncate *> migrator.clean() *> migrator.migrate()))
      .value
      .flatMap(_ => fa(locator))

  }.unsafeRunSync()
}
