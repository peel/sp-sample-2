package com.codearsonist.plotacsv

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.server.middleware._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import fs2.{StreamApp, io, text}
import java.nio.file.{ Path => JPath, Paths }
import cats.implicits._
import cats.effect.{IO, Sync}

import domain._

object domain {
  type Date = String
  type Percent = String
  case class Metric(date: Date, `yield`: Percent)
}

object config {
  val path: JPath = Paths.get("data.csv")
}

object Service extends Service
trait Service {
  private val load: JPath => fs2.Stream[IO,Byte] = path => fs2.io.file.readAll[IO](path, 4096)
  protected val parseLine: (String, Long) => Metric = (line,n) => line.split("\t") match {
    case Array(date, _, _, _, _, _, y, _) =>  Metric(date,y)
    case _ => throw new Exception(s"""Line ${n+1}: CSV parse error: "$line"""")
  }
  protected val parse: fs2.Stream[IO,Byte] => IO[Json] = stream => stream
    .through(fs2.text.utf8Decode)
    .through(fs2.text.lines)
    .filter(s => !s.trim.isEmpty)
    .drop(1)
    .zipWithIndex
    .map{ case (line, i) => parseLine(line, i) }
    .runFold(List.empty[Metric]) { case (acc, str) => str :: acc }
    .map(_.asJson)

  val handle = load >>> parse
}

object Server extends StreamApp[IO] with Http4sDsl[IO] {
  import Service._
  val service = HttpService[IO] {
    case GET -> Root / "yields" =>
      Ok(Service.handle(config.path).unsafeRunSync)
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(CORS(service), "/")
      .serve
}
