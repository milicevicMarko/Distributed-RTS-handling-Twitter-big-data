package kafka.kafka_consumer

import akka.actor.typed.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.Subscriptions
import akka.stream.scaladsl.Source
import kafka.connection_properties.DefaultConnectionProperties
import org.apache.kafka.clients.consumer.ConsumerRecord

object KafkaConsumerConnection {
  private val kafkaUri = sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVER", "localhost:9092")
  private val groupId = "test"
  private val topic = "results-topic"

  def apply(sessionId: String)(implicit actorSystem: ActorSystem[Nothing]): Source[ConsumerRecord[String, String], Consumer.Control] = {
    val kafkaConsumerSettings = DefaultConnectionProperties(actorSystem, kafkaUri, groupId)
    Consumer
      .plainSource(kafkaConsumerSettings, Subscriptions.topics(topic))
      .filter(record => {
        sessionId == record.key()})
  }
}