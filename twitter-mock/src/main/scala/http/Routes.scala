package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import io_utils.ImportJsonUtil.getRandom
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.duration._

case class RulesData(rules: String)

trait RulesDataSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val sessionDataFormat: RootJsonFormat[RulesData] = jsonFormat1(RulesData)
}

object Routes extends Directives with RulesDataSupport {


  private val corsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)

  private val source: Source[ByteString, _] =
    Source.tick(1.second, 1.second, ())
      .map(_ => {
        val rndTweet = getRandom
        println("Sending: " + rndTweet)
        ByteString(rndTweet + "\n")
      })
  def apply(): Route = cors(corsSettings) {
    path("hello") {
    get {
      complete {
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello, World!")
      }
    }
  } ~ path("2" / "tweets" / "sample" / "stream") {
    get {
      complete {
        println("Mock server: sending tweets")

        HttpEntity.Chunked.fromData(ContentTypes.`application/json`, source)
      }
    }
  } ~ path("2" / "tweets" / "search" / "stream") {
    get {
      complete {
        println("Mock server: sending tweets")
        println("Mock server: sending filtered stream")

        HttpEntity.Chunked.fromData(ContentTypes.`application/json`, source)
      }
    }
  } ~ path("2" / "tweets" / "search" / "stream" / "rules") {
    post {
      entity(as[RulesData]) { rules =>
        complete {
          println("Mock server: sending rules: ", rules)
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Updating rules")
        }
      }
    }
  }
}
}
