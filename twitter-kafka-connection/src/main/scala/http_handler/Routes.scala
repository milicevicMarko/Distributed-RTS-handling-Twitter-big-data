package http_handler

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import session_management.SessionSystem.SessionSystemCommand

class Routes(system: ActorSystem[SessionSystemCommand]) {
  private val corsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)

  val route: Route = cors(corsSettings) {
    StreamRoutes() ~ StructureRoute("structure")
  }
}
