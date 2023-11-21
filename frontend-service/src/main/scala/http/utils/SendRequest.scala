package http.utils

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import http.entities._

import scala.concurrent.{ExecutionContext, Future}

object SendRequest extends JsonDataSupport {
  import http.WebServer.Constants.TIMEOUT
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Gateway")
  implicit val executionContext: ExecutionContext = system.executionContext

  private def createRequest[T <: JsonData](jsonData: T, uri: String): HttpRequest = HttpRequest(
    method = HttpMethods.POST,
    uri = uri,
    entity = HttpEntity(
      ContentTypes.`application/json`,
      toJson(jsonData).toString()
    )
  )

  def apply(jsonData: JsonData, uri: String): Future[String] = Http()
    .singleRequest(createRequest(jsonData, uri))
    .flatMap(response => response.entity.toStrict(TIMEOUT))
    .map(entity => entity.data.utf8String)
}
