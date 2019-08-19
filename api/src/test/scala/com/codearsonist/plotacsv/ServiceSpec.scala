package com.codearsonist.plotacsv

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.scalatest._
import org.scalatest.prop._
import org.scalatest.concurrent._
import org.scalatest.Matchers._
import org.scalacheck._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import cats._
import cats.data._
import cats.implicits._
import fs2.{io,text}

class ServiceSpec extends WordSpec with PropertyChecks with Matchers {
  implicit val noShrink: Shrink[String] = Shrink.shrinkAny
  object ServiceT extends Service {
    val parseLineT = this.parseLine
    val parseT = this.parse
  }
  val line = for {
    date <- Gen.alphaStr.suchThat(_.nonEmpty)
    c2 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c3 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c4 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c5 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c6 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c7 <- Gen.alphaStr.suchThat(_.nonEmpty)
    c8 <- Gen.alphaStr.suchThat(_.nonEmpty)
  } yield s"""$date\t$c2\t$c3\t$c4\t$c5\t$c6\t$c7\t$c8"""

  "Service" can {
    "parse a line string into a metric" in {
      forAll(line,Gen.posNum[Long]){ ( line: String, n: Long ) =>
        ServiceT.parseLineT(line, n) shouldBe a [domain.Metric]
      }
    }
    "parse a stream of data" in {
      forAll(Gen.listOf(line)){ seq: List[String] =>
        val stream = fs2.Stream.eval(IO {seq.mkString("\n")}).through(text.utf8Encode)
        ServiceT.parseT(stream).unsafeRunSync shouldBe a [Json$JArray]
      }
    }
  }
}
