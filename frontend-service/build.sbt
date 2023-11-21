ThisBuild / version := "1.1"

ThisBuild / scalaVersion := "2.12.14"

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

lazy val root = (project in file("."))
  .settings(
    name := "frontend-service"
  )

Compile / mainClass := Some("Main")
Docker / packageName := "frontend-service"
dockerExposedPorts ++= Seq(8083)
dockerBaseImage := "openjdk:11"

val akkaHttpVersion = "10.5.0"
val akkaVersion = "2.8.1"
val kafkaVersion = "3.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "org.apache.kafka" % "kafka-clients" % kafkaVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaVersion,
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.0",

  "ch.megard" %% "akka-http-cors" % "1.2.0",

  "ch.qos.logback" % "logback-classic" % "1.4.7",

  "com.typesafe.play" %% "play-json" % "2.9.4"
)

resolvers ++= Seq(
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
)
