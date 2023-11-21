package http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import http.entities.{SessionData, JsonDataSupport}
import http.utils.SendRequest

import scala.util.{Failure, Success}

object SessionStopRoute extends Directives with JsonDataSupport {
  import Routes.Constants.{SESSION_STOP, TWITTER_CONNECTION_URI}

  def apply(): Route =
    path(SESSION_STOP) {
      post {
        entity(as[SessionData]) { data =>
          val sessionId = data.sessionId
          println(s"Received session id: $sessionId")
          onComplete(SendRequest(data, TWITTER_CONNECTION_URI + SESSION_STOP)) {
            case Success(result) =>
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result))
            case Failure(exception) =>
              complete(500, s"An error occurred: ${exception.getMessage}")
          }
        }
      }
    }
}
