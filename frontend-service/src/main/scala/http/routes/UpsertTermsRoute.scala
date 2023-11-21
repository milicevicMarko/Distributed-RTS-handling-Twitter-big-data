package http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import http.entities.{JsonDataSupport, StructureData}
import http.utils.SendRequest

import scala.util.{Failure, Success}

object UpsertTermsRoute extends Directives with JsonDataSupport {

  import Routes.Constants.TWITTER_CONNECTION_URI

  def apply(endpoint: String): Route =
    path(endpoint) {
      post {
        entity(as[StructureData]) { data =>
          println(s"UpsertTermsRoute: $data")
          println(s"UpsertTermsRoute: $endpoint")
          val termList = data.terms
          val termListIsValid = termList.length > 0 && termList.length < 5

          if (termListIsValid) {
            onComplete(
              SendRequest(data, TWITTER_CONNECTION_URI + endpoint)) {
                case Success(result) =>
                  complete(200, HttpEntity(ContentTypes.`text/plain(UTF-8)`, result))
                case Failure(exception) =>
                  complete(500, s"An error occurred: ${exception.getMessage}")
              }
          } else {
            complete(500, s"An error occurred: You need to pass at least one term.")
          }
        }
      }
    }
}
