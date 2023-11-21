ThisBuild / version := "1.0.1"

ThisBuild / scalaVersion := "2.12.14"

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

lazy val root = (project in file("."))
  .settings(
    name := "twitter-kafka-connection"
  )

Compile / mainClass := Some("Main")
Docker / packageName := "twitter-kafka-connection"
dockerExposedPorts ++= Seq(8084)
dockerBaseImage := "openjdk:11"

libraryDependencies ++= {
  val sparkVersion = "3.3.2"
  val kafkaVersion = "3.4.0"
  val akkaVersion = "2.8.1"
  val akkaHttpVersion = "10.5.0"
  val twitterApiVersion = "2.0.3"

  Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVersion,
    "org.apache.kafka" % "kafka-streams" % kafkaVersion,
    "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
    
    "com.twitter" % "twitter-api-java-sdk" % twitterApiVersion,

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
