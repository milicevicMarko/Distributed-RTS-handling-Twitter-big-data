package http_handler

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import session_management.SessionSystem
import session_management.SessionSystem.{SessionSystemCommand, UpdateSessionRules}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContextExecutor

case class StructureData(sessionId: String, terms: Array[String])

trait StructureDataSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val structureDataFormat: RootJsonFormat[StructureData] = jsonFormat2(StructureData)
}

object StructureRoute extends Directives with StructureDataSupport {
  implicit val system: ActorSystem[SessionSystemCommand] = SessionSystem.system
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  def apply(endpoint: String): Route =
  path (endpoint) {
    post {
      entity(as[StructureData]) { structureData =>
        val sessionId = structureData.sessionId
        val termList = structureData.terms.map(i => "(" + i +")").mkString(" OR ")
        system ! UpdateSessionRules(sessionId, termList)
        system.log.info(s"Updating stream for session: $sessionId")
        complete(200, s"Received structure: $termList")
      }
    }
  }
}