package session_management

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import session_management.SessionActor._

object SessionSupervisorActor {
  sealed trait SupervisorCommand
  case class SupervisorStartSession(sessionId: String, terms: String) extends SupervisorCommand
  case class SupervisorEndSession(sessionId: String) extends SupervisorCommand
  case class SupervisorUpdateSessionRules(sessionId: String, terms: String) extends SupervisorCommand

  // Creating a separate actor tree for each user, where each user has its own set of child actors, allows you to isolate the state and behavior of each user.
  // This design pattern is known as the "Per Actor" pattern, where each entity or user is represented by its own actor and encapsulates its state and behavior.

  private var sessions: Map[String, ActorRef[SessionActor.SessionCommand]] = Map.empty
  def apply(): Behavior[SupervisorCommand] = Behaviors.receive { (context, message) =>
    message match {
      case SupervisorStartSession(sessionId, terms) =>
        val sessionActor = context.spawn(SessionActor(sessionId, terms), sessionId)
        sessionActor ! StartSession
        sessions += sessionId -> sessionActor
        Behaviors.same
      case SupervisorEndSession("all") =>
        sessions.foreach(session => {
          context.log.info("Ending session")
          session._2 ! EndSession
        })
        Behaviors.same
      case SupervisorUpdateSessionRules(sessionId, terms) =>
        context.log.info("Updating session rules")
        try {
          sessions(sessionId) ! UpdateSessionRules(terms)
        }
        catch
        {
          case _: NoSuchElementException =>
            context.log.error(s"Session $sessionId not found")
        }
        Behaviors.same
      case SupervisorEndSession(sessionId) =>
        context.log.info("Ending session")
        try {
          sessions(sessionId) ! EndSession
          context.stop(sessions(sessionId))
        } catch {
          case _: NoSuchElementException =>
            context.log.error(s"Session $sessionId not found")
        }
        Behaviors.same
    }
  }
}

