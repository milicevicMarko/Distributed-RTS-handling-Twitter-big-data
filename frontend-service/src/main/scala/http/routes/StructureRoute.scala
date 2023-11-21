package http.routes

import akka.http.scaladsl.server.Route

object StructureRoute {
  import Routes.Constants.STRUCTURE
  def apply(): Route = UpsertTermsRoute(STRUCTURE)
}
