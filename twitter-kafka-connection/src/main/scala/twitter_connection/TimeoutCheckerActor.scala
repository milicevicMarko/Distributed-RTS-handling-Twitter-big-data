package twitter_connection

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import twitter_connection.TimeoutCheckerActor.TIMEOUT_MILLIS

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object TimeoutCheckerActor {
  sealed trait TimeoutCheckerCommand

  case object Start extends TimeoutCheckerCommand

  case object End extends TimeoutCheckerCommand

  case object ResetTime extends TimeoutCheckerCommand

  private case object Check extends TimeoutCheckerCommand

  private val TIMEOUT_MILLIS: FiniteDuration = 5.seconds
  private val DELAY_MILLIS: FiniteDuration = 1000.millis
  private val SLEEP_MILLIS: FiniteDuration = 1000.millis
}

class TimeoutCheckerActor(val executorActor: ActorRef[ExecutorsActor.ExecutorCommand], val timeoutMillis: FiniteDuration = TIMEOUT_MILLIS)  {

  import TimeoutCheckerActor._

  private var timeStarted: Long = System.currentTimeMillis

  private def resetTime(): Unit = {
    timeStarted = System.currentTimeMillis
  }

  private def checkTimes(): Unit = {
    def shouldStop: Boolean = (System.currentTimeMillis - timeStarted) > timeoutMillis.toMillis

    if (shouldStop) {
      println("TimeoutCheckerActor: Timeout")
      executorActor ! ExecutorsActor.Timeout
    }
  }

  def apply(): Behavior[TimeoutCheckerCommand] = Behaviors.receive {
    (context, message) =>
      message match {
        case Start =>
          Behaviors.withTimers(timers => {
            context.log.info("Timeout Checker Starting")
            timers.startTimerWithFixedDelay(Check, DELAY_MILLIS, SLEEP_MILLIS)
            Behaviors.same
          })
        case End =>
          context.log.info("Timeout Checker Ended")
          Behaviors.withTimers(timers => {
            timers.cancelAll()
            Behaviors.stopped
          })
        case Check =>
          context.log.info("Timeout Checker Checking")
          checkTimes()
          Behaviors.same
        case ResetTime =>
          context.log.info("Timeout Checker Resetting time")
          resetTime()
          Behaviors.same
      }
  }
}
