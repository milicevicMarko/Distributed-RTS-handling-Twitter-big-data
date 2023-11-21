package kafka.connection_properties

import org.apache.kafka.clients.consumer.ConsumerConfig

import java.util.Properties

case class ConnectionConfigProperties(topic: String, config: Map[String, String]) {
  def this() = this(DefaultConnectionProperties.DATA_TOPIC, DefaultConnectionProperties.DEFAULT_CONFIG)
  def this(topic: String) = this(topic, DefaultConnectionProperties.DEFAULT_CONFIG)
  def this(config: Map[String, String]) = this(DefaultConnectionProperties.DATA_TOPIC, config)

  val properties: Properties = new Properties()
  config.foreach({ case (key, value) => properties.put(key, value) })
}

object DefaultConnectionProperties {
  val SERVER: String = sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVER", "kafka:29092")

  val DATA_TOPIC: String = "twitter-topic"
  val STRUCTURE_TOPIC: String = "stream-structure-topic"
  val RESULT_TOPIC: String = "result-topic"

  val DEFAULT_CONFIG: Map[String, String] = Map(
    ConsumerConfig.GROUP_ID_CONFIG -> "test",
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> SERVER,
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
    ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "1000"
  )
}