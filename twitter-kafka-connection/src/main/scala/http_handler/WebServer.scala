package http_handler

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import session_management.SessionSystem
import session_management.SessionSystem.SessionSystemCommand

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object WebServer {
  private val INTERFACE = "0.0.0.0"
  private val PORT = 8084

  def startHttpServer(): Unit = {
    implicit val system: ActorSystem[SessionSystemCommand] = SessionSystem.system
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val routes = new Routes(system).route

    val httpBindingFuture = Http().newServerAt(INTERFACE, PORT).bindFlow(routes)

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
