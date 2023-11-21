package session_management

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import kafka.connection_properties.ConnectionConfigProperties
import kafka.kafka_producer.KafkaProducerActor
import twitter_connection.{ExecutorsActor, StreamHandlerActor, StreamStructure, StreamStructureBuilder, TweetsReaderActor, TwitterInputStream}

object SessionActor {
  sealed trait SessionCommand
  case object StartSession extends SessionCommand
  case object EndSession extends SessionCommand

  case class UpdateSessionRules(rules: String) extends SessionCommand

  def apply(sessionId: String, rules: String): Behavior[SessionCommand] = {
    Behaviors.setup { context =>
      context.log.info(s"Creating session [$sessionId]...")
      val kafkaProducerActor = context.spawn(new KafkaProducerActor(new ConnectionConfigProperties).apply(sessionId), "kafkaProducerActor")
      val executorActor = context.spawn(new ExecutorsActor().apply(), "executorActor")
      val streamHandlerActor = context.spawn(new StreamHandlerActor(executorActor, kafkaProducerActor).apply(), "streamHandlerActor")
      val readerActor = context.spawn(new TweetsReaderActor(executorActor, streamHandlerActor).apply(rules), "TweetsReaderActor")

      Behaviors.receiveMessage[SessionCommand] {
        case StartSession =>
          context.log.error(s"Starting session: $sessionId")
          kafkaProducerActor ! KafkaProducerActor.SendStreamStructure(rules)
          executorActor ! ExecutorsActor.Start
          readerActor ! TweetsReaderActor.StartReader
          Behaviors.same
        case UpdateSessionRules(rules) =>
          kafkaProducerActor ! KafkaProducerActor.SendStreamStructure(rules)
          readerActor ! TweetsReaderActor.UpdateReader(rules)
          Behaviors.same
        case EndSession =>
          context.log.error(s"Ending session: $sessionId")
          readerActor ! TweetsReaderActor.EndReader
          executorActor ! ExecutorsActor.End
          kafkaProducerActor ! KafkaProducerActor.CloseConnection
          Behaviors.stopped
      }
    }
  }
}
