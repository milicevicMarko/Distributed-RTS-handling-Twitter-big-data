package session_management

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object SessionSystem {
  sealed trait SessionSystemCommand
  case class StartSession(sessionId: String, terms: String) extends SessionSystemCommand
  case class EndSession(sessionId: String) extends SessionSystemCommand

  case class UpdateSessionRules(sessionId: String, terms: String) extends SessionSystemCommand

  val system: ActorSystem[SessionSystemCommand] = ActorSystem(SessionSystem(), "SessionSystem")

  def apply(): Behavior[SessionSystemCommand] = Behaviors.setup { context =>
    val supervisorActor = context.spawn(SessionSupervisorActor(), "SupervisorActor")

    Behaviors.receiveMessage {
      case StartSession(sessionId: String, terms: String) =>
        context.log.info("Starting session")
        supervisorActor ! SessionSupervisorActor.SupervisorStartSession(sessionId, terms)
        Behaviors.same
      case UpdateSessionRules(sessionId: String, terms: String) =>
        context.log.info("Received terms")
        supervisorActor ! SessionSupervisorActor.SupervisorUpdateSessionRules(sessionId, terms)
        Behaviors.same
      case EndSession(sessionId: String) =>
        context.log.info("Ending session")
        supervisorActor ! SessionSupervisorActor.SupervisorEndSession(sessionId)
        Behaviors.same
    }
  }
}
