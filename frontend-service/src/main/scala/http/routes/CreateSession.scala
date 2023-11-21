package http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout

import java.util.UUID

object CreateSession extends Directives {
  import http.WebServer.Constants.TIMEOUT
  import Routes.Constants.SESSION
  implicit val timeout: Timeout = TIMEOUT

  def apply(): Route =
    path(SESSION) {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, UUID.randomUUID().toString))
      }
    }
}
