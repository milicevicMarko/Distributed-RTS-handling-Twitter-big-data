package twitter_connection

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration.DurationInt

object TweetQueueActor {
  sealed trait TwQueueCommand

  case object StartQueue extends TwQueueCommand

  case object StopQueue extends TwQueueCommand

  case class Enqueue(streamingTweet: String) extends TwQueueCommand

  case object Flush extends TwQueueCommand

  case object PollUntilEmpty extends TwQueueCommand
  private case object TryToPoll extends TwQueueCommand

  private case object Poll extends TwQueueCommand
}

class TweetQueueActor() {
  import TweetQueueActor._

  def apply(streamingActor: ActorRef[StreamHandlerActor.StreamHandlerCommand]): Behavior[TwQueueCommand] = Behaviors.setup {
    _ =>
      val tweetsQueue = new QueueWrapper()


    Behaviors.receive { (context, message) =>
      message match {
        case StartQueue =>
          Behaviors.withTimers(timers => {
            context.log.info("Starting Queue Reader")
            timers.startTimerWithFixedDelay(TryToPoll, 1000.millis, 100.millis)
            Behaviors.same
          })
        case StopQueue =>
          context.log.info("Ending")
          context.self ! Flush
          Behaviors.stopped
        case Poll =>
          context.log.info("Polling")
          val tweetString = tweetsQueue.poll
          if (tweetString.isEmpty) {
            context.log.warn("Polling empty queue")
          } else {
            streamingActor ! StreamHandlerActor.ProcessStreamingObject(tweetString.get)
          }
          Behaviors.same
        case TryToPoll =>
          context.log.info("Trying to poll")
          if (!tweetsQueue.isEmpty) {
            context.self ! Poll
          }
          Behaviors.same
        case Enqueue(streamingTweet) =>
          context.log.info("Enqueue: "+streamingTweet)
          tweetsQueue.enqueue(streamingTweet)
          Behaviors.same
        case Flush =>
          // Drops everything, empties the queue
          context.log.info("Flushing")
          tweetsQueue.flush()
          Behaviors.stopped
        case PollUntilEmpty =>
          // Polls everything, empties the queue
          context.log.info("Polling until empty")
          if (!tweetsQueue.isEmpty) {
            context.self ! Poll
            context.scheduleOnce(20.millis, context.self, PollUntilEmpty)
          }
          Behaviors.same
      }
    }
  }
}
