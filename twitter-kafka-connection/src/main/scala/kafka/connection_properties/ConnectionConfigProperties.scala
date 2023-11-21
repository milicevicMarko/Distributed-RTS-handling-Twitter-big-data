package kafka.connection_properties

import org.apache.kafka.clients.producer.ProducerConfig

import java.util.Properties

case class ConnectionConfigProperties(topic: String, config: Map[String, String]) {
  def this() = this(DefaultConnectionProperties.DEFAULT_TOPIC, DefaultConnectionProperties.DEFAULT_CONFIG)
  def this(topic: String) = this(topic, DefaultConnectionProperties.DEFAULT_CONFIG)
  def this(config: Map[String, String]) = this(DefaultConnectionProperties.DEFAULT_TOPIC, config)

  val properties: Properties = new Properties()
  config.foreach({ case (key, value) => properties.put(key, value) })
}

object DefaultConnectionProperties {
  val DEFAULT_TOPIC: String = "twitter-topic"
  val DEFAULT_STREAM_STRUCTURE_TOPIC: String = "stream-structure-topic"
  private val bootstrapServer = sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVER", "localhost:9092")

  val DEFAULT_CONFIG: Map[String, String] = Map(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServer,
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer",
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer",
    ProducerConfig.ACKS_CONFIG -> "all"
  )
}