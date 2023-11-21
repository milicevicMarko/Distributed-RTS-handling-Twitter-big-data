package twitter_connection

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object ExecutorsActor {

  sealed trait ExecutorCommand

  case object Start extends ExecutorCommand
  case object End extends ExecutorCommand

  case object ResetTime extends ExecutorCommand

  case object Timeout extends ExecutorCommand

  case class Error(errorMessage: String) extends ExecutorCommand

  case object Reconnect extends ExecutorCommand
}

class ExecutorsActor() {
  import ExecutorsActor._
  def apply(): Behavior[ExecutorCommand] = Behaviors.setup {
    context =>
      val timeoutChecker = context.spawn(
        new TimeoutCheckerActor(executorActor=context.self).apply(), "TimeoutCheckerActor")

      Behaviors.receive {
      (context, message) =>
        message match {
          case Start =>
            context.log.info("Starting")
            timeoutChecker ! TimeoutCheckerActor.Start
            Behaviors.same
          case ResetTime =>
            context.log.info("Resetting time")
            timeoutChecker ! TimeoutCheckerActor.ResetTime
            Behaviors.same
          case End =>
            context.log.info("Ending")
            timeoutChecker ! TimeoutCheckerActor.End
            Behaviors.stopped
          case Reconnect =>
            // todo revisit
            context.log.info("Reconnecting")
            // create new tweets reader
            val readerActor: ActorRef[TweetsReaderActor.TwReaderCommand] = context.child("TweetsReaderActor").get.asInstanceOf[ActorRef[TweetsReaderActor.TwReaderCommand]]
            readerActor ! TweetsReaderActor.EndReader
//            streamActor ! StreamHandlerActor.StartStreaming
            Behaviors.same
          case Timeout =>
            context.log.info("Timeout")
            context.self ! End
            Behaviors.same
          case Error(errorMessage: String) =>
            context.log.error("Error")
            context.log.error(errorMessage)
            context.self ! End
            Behaviors.same
        }
    }
  }
}
