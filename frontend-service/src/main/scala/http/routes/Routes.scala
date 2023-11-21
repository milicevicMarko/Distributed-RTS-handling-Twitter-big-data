package http.routes

import akka.http.scaladsl.server.{Directives, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

object Routes extends Directives {
  object Constants {
    val TWITTER_CONNECTION_URI = sys.env.getOrElse("TWITTER_CONNECTION_URI", "localhost:8084")
    val SESSION_START = "start"
    val SESSION_STOP = "stop"
    val STRUCTURE = "structure"
    val TWEETS = "tweets"
    val SESSION = "session"
  }

  private val corsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)

  def apply(): Route = cors(corsSettings) {
      CreateSession() ~
      SessionStartRoute() ~
      SessionStopRoute() ~
      StructureRoute() ~
      TweetsRoute()
  }
}
