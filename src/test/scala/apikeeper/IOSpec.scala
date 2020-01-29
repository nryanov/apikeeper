package apikeeper

import cats.effect.IO
import org.scalatest.Assertion

trait IOSpec extends BaseSpec {
  type F[A] = IO[A]

  val fixedUUID: FixedUUID[F] = FixedUUID[F]()

  def runF(body: F[Assertion]): Assertion = body.unsafeRunSync()
}
