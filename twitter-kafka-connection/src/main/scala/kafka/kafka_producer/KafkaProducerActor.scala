package kafka.kafka_producer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import kafka.connection_properties.ConnectionConfigProperties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaProducerActor {
  trait KafkaProducerCommand

  case class SendTweet(message: String) extends KafkaProducerCommand

  case class SendStreamStructure(rules: String) extends KafkaProducerCommand

  private case object CheckConnection extends KafkaProducerCommand

  case object CloseConnection extends KafkaProducerCommand
}
class KafkaProducerActor(val connectionConfig: ConnectionConfigProperties) {
  import KafkaProducerActor._

  def apply(sessionId: String): Behavior[KafkaProducerCommand] = Behaviors.setup {
    _ =>
    val producer:KafkaProducer[String, String] = new KafkaProducer[String, String](connectionConfig.properties)

    Behaviors.receive { (context, message) =>
      message match {
        case CheckConnection =>
          context.log.info("Checking connection")
          producer.flush() // todo?
          Behaviors.same
        case CloseConnection =>
          context.log.info("Closing connection")
          producer.flush()
          producer.close()
          Behaviors.same
        case SendTweet(message) =>
          context.log.info("Producing")
          producer.send(new ProducerRecord[String, String](connectionConfig.topic, sessionId, message))
          Behaviors.same
        case SendStreamStructure(structure) =>
          context.log.info("Producing Stream Structure")
          producer.send(new ProducerRecord[String, String]("stream-structure-topic", sessionId, structure))
          Behaviors.same
      }
    }
  }
}
