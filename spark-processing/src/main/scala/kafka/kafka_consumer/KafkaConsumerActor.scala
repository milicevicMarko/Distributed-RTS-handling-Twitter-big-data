package kafka.kafka_consumer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import kafka.connection_properties.ConnectionConfigProperties
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration

object KafkaConsumerActor {
  trait KafkaConsumerCommand

  case object StartConsumer extends KafkaConsumerCommand
  private case object TryToConsume extends KafkaConsumerCommand

  case object CloseConnection extends KafkaConsumerCommand

  private val POLLING_DURATION: FiniteDuration = FiniteDuration(100, "millis")
  private val INITIAL_DELAY: FiniteDuration = FiniteDuration(101, "millis")
}

class KafkaConsumerActor(val connectionConfig: ConnectionConfigProperties) {

  import KafkaConsumerActor._

  def apply(): Behavior[KafkaConsumerCommand] = Behaviors.setup {
    context =>
      val consumer: KafkaConsumer[String, String] = new KafkaConsumer(connectionConfig.properties)

      context.log.info("Successfully created Kafka Consumer")
    Behaviors.receive { (context, message) =>
      message match {
        case StartConsumer =>
          Behaviors.withTimers(timers => {
            context.log.info("Starting Kafka Consumer")
            consumer.subscribe(List(connectionConfig.topic).asJava)
            context.log.info(s"Consumer subscribed to topic - ${connectionConfig.topic}")
            timers.startTimerWithFixedDelay(TryToConsume, INITIAL_DELAY, POLLING_DURATION)
            Behaviors.same
          })
        case TryToConsume =>
          context.log.info(s"Polling for: $POLLING_DURATION")
          // pulls in a batch of records
          val batch = consumer.poll(Duration.ofMillis(POLLING_DURATION.toMillis))
          if (!batch.isEmpty) {
            batch.forEach(record => context.log.info("@_@ " + record.value()))
          } else {
            context.log.info("No records")
          }
          Behaviors.same
        case CloseConnection =>
          context.log.info("Closing connection")
          consumer.close()
          Behaviors.stopped
      }
    }
  }
}