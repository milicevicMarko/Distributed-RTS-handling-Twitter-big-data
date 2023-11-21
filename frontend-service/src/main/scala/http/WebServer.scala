package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import http.routes.Routes

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.{Failure, Success}

object WebServer {
  object Constants {
    val TIMEOUT: FiniteDuration = 3.seconds
    val REFRESH_INTERVAL: FiniteDuration = 1.seconds
    val INTERFACE = "0.0.0.0"
    val PORT = 8083
  }
  import Constants._

  def startHttpServer()(implicit system: ActorSystem): Unit = {
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val routes = Routes()

    val httpBindingFuture = Http().newServerAt(INTERFACE, PORT).bind(routes)

    httpBindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(exception) =>
        system.log.error(s"Server could not start!")
        exception.printStackTrace()
        system.terminate()
    }
  }
}
