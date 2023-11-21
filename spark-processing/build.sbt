ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.12.14"

enablePlugins(DockerPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "spark-processing"
  )

libraryDependencies ++= {
  val sparkVersion = "3.3.2"
  val kafkaVersion = "3.4.0"
  val akkaVersion = "2.8.1"
  val akkaHttpVersion = "10.5.0"

  Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVersion,
    "org.apache.kafka" % "kafka-streams" % kafkaVersion,
    "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,

    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,

    "ch.qos.logback" % "logback-classic" % "1.4.7",
  )
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}