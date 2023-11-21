ThisBuild / version := "1.1"

ThisBuild / scalaVersion := "2.12.14"

lazy val root = (project in file("."))
  .settings(
    name := "twitter-mock"
  )

ThisBuild / scalaVersion := "2.12.14"

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

Compile / mainClass := Some("http/MockServer")
Docker / packageName := "twitter-mock"
dockerExposedPorts ++= Seq(8085)
dockerBaseImage := "openjdk:11"

libraryDependencies ++= {
  val akkaVersion = "2.8.1"
  val akkaHttpVersion = "10.5.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,

    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    "ch.megard" %% "akka-http-cors" % "1.2.0",

    "ch.qos.logback" % "logback-classic" % "1.4.7",

    "com.typesafe.play" %% "play-json" % "2.9.4"
  )
}

resolvers ++= Seq(
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
)
