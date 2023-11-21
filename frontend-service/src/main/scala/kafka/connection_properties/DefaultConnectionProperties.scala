package kafka.connection_properties

import akka.actor.typed.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.common.serialization.StringDeserializer

object DefaultConnectionProperties {
  def apply(actorSystem: ActorSystem[Nothing], kafkaUri: String, groupId: String): ConsumerSettings[String, String] = {
    ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaUri)
      .withGroupId(groupId)
      .withProperty("auto.offset.reset", "earliest")
      .withProperty("auto.commit.interval.ms", "1000")
      .withProperty("enable.auto.commit.config", "true")
  }
}