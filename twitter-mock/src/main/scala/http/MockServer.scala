package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContextExecutor

object MockServer extends Directives with RulesDataSupport {
  private object Constants {
    val PORT = 8085
    val HOST = "0.0.0.0"
  }

  import Constants._

  private def startHttpServer()(implicit system: ActorSystem): Unit = {
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val routes = Routes()

    val httpBindingFuture = Http().newServerAt(HOST, PORT).bind(routes)

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

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("Mock-Twitter-Replies")

    MockServer.startHttpServer()
  }
}

