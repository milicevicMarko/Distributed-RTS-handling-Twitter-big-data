package http.routes

import akka.http.scaladsl.server.Route

object SessionStartRoute {
  import Routes.Constants.SESSION_START
  implicit
  def apply(): Route = UpsertTermsRoute(SESSION_START)
}
