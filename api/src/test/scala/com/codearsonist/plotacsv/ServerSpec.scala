package com.codearsonist.plotacsv

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.scalatest._
import org.scalatest.prop._
import org.scalatest.concurrent._
import org.scalatest.Matchers._
import org.scalacheck._
import cats._
import cats.data._
import cats.implicits._

class ServerSpec extends WordSpec with PropertyChecks with Matchers {
  "Plot-A-Paper" can {
    "return 200" in {
      ret.status should equal(Status.Ok)
    }
    "return graph data" in {
      ret.as[String].unsafeRunSync() should startWith("""[{"date":""")
    }
  }

  private[this] val ret: Response[IO] = {
    val get = Request[IO](Method.GET, Uri.uri("/yields"))
    Server.service.orNotFound(get).unsafeRunSync()
  }
}
