package http_handler

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import session_management.SessionSystem
import session_management.SessionSystem.{EndSession, SessionSystemCommand, StartSession}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContextExecutor

case class StartSessionData(sessionId: String, terms: Array[String])
case class StopSessionData(sessionId: String)

trait StartSessionDataSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val startSessionDataFormat: RootJsonFormat[StartSessionData] = jsonFormat2(StartSessionData)
}

trait StopSessionDataSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val stopSessionDataFormat: RootJsonFormat[StopSessionData] = jsonFormat1(StopSessionData)
}

object StreamRoutes extends Directives with StartSessionDataSupport with StopSessionDataSupport {
  implicit val system: ActorSystem[SessionSystemCommand] = SessionSystem.system
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  def apply(): Route =
    path("start") {
      post {
        entity(as[StartSessionData]) { data =>
          complete {
            val sessionId = data.sessionId
            val terms = data.terms.map(i => "(" + i +")").mkString(" OR ")
            system ! StartSession(sessionId, terms)
            system.log.info(s"Started stream for session - $sessionId with terms: $terms")
            HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Started stream for session: $sessionId")
          }
        }
      }
    } ~ path("stop") {
      post {
        entity(as[StopSessionData]) { data =>
          complete {
            val sessionId = data.sessionId
            system ! EndSession(sessionId)
            system.log.info(s"Stopped stream for session: $sessionId")
            HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Stopped stream for session: $sessionId")
          }
        }
      }
    } ~ path("stop-all") {
      post {
        complete {
          system.log.info(s"Stopped all streams")
          system ! EndSession("all")
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Stopped all streams")
        }
      }
  }
}
