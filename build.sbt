
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.12"

name := "data-finder"

lazy val akkaHttpVersion = "10.2.6"
lazy val akkaVersion = "2.6.0"
lazy val akkaManagementVersion = "1.0.9"

// make version compatible with docker for publishing
ThisBuild / dynverSeparator := "-"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")
classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
fork in run := true
Compile / run / fork := true

mainClass in (Compile, run) := Some("ua.net.ipk0")

enablePlugins(JavaServerAppPackaging, DockerPlugin)

dockerExposedPorts := Seq(8080)
dockerUpdateLatest := true
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerBaseImage := "adoptopenjdk:11-jre-hotspot"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.6",
    "com.github.swagger-akka-http" %% "swagger-akka-http-with-ui" % "2.6.0",
    "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.0.0",
    "com.github.swagger-akka-http" %% "swagger-akka-http-with-ui" % "2.6.0",
    "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.5.0",
    "com.github.swagger-akka-http" %% "swagger-enumeratum-module" % "2.3.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0",
    "com.bucket4j" % "bucket4j-core" % "8.1.0",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % "2.6.16" % Test,
    "org.scalatestplus" %% "mockito-5-10" % "3.2.18.0" % "test",
    "org.scalatest" %% "scalatest" % "3.0.9" % Test
  )
}
