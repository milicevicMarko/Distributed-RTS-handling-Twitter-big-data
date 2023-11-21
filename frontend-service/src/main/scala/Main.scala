import akka.actor.ActorSystem
import http.WebServer

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("Gateway")

    WebServer.startHttpServer()
  }
}
