package http.routes

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import http.WebServer.Constants.{REFRESH_INTERVAL, TIMEOUT}
import http.entities.JsonDataSupport
import kafka.kafka_consumer.KafkaConsumerConnection
import play.api.libs.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

private case class KafkaMessage(sessionId: String, message: String)

object TweetsRoute extends Directives with JsonDataSupport {
  import Routes.Constants.TWEETS

  implicit val timeout: FiniteDuration = TIMEOUT
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Gateway")
  implicit val executionContext: ExecutionContext = system.executionContext

  private def packageKafkaMessage(msg: KafkaMessage): String = {
    Json.stringify(Json.obj(
      "sessionId" -> msg.sessionId,
      "message" -> Json.parse(msg.message)
    ))
  }

  def apply(): Route =
    path(TWEETS) {
      get {
        parameters(Symbol("session").as[String]) { sessionId =>
          complete {
            KafkaConsumerConnection(sessionId)
              .map { consumerRecord =>
                ServerSentEvent(packageKafkaMessage(KafkaMessage(consumerRecord.key(), consumerRecord.value())))
              }
              .keepAlive(REFRESH_INTERVAL, () => ServerSentEvent.heartbeat)
          }
        }
      }
    }
}
