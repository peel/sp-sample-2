val Http4sVersion = "0.18.3"
val Fs2Version = "0.10.3"
val ScalaTestVersion = "3.0.5"
val ScalacheckVersion = "1.13.5"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.9.2"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    organization := "com.codearsonist",
    name := "plot-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.5",
    version in Docker := "latest",
    dockerUsername := Some("peelsky"),
    dockerBaseImage := "openjdk:8-jre-slim",
    mappings in Universal += file("data.csv") -> "data.csv",
    libraryDependencies ++= Seq(
      "co.fs2"          %% "fs2-io"              % Fs2Version,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalatest"   %% "scalatest"           % ScalaTestVersion % "test",
      "org.scalacheck"  %% "scalacheck"          % ScalacheckVersion % "test",
    )
  )
