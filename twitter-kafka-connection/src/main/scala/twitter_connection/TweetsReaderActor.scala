package twitter_connection

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import twitter_connection.TwitterInputStream.endStream

import java.io.{BufferedReader, InputStreamReader}
import scala.concurrent.duration.DurationInt

object TweetsReaderActor {
  sealed trait TwReaderCommand

  case object StartReader extends TwReaderCommand

  case class UpdateReader(terms: String) extends TwReaderCommand

  private case object QueueTweets extends TwReaderCommand

  case object EndReader extends TwReaderCommand

  private case class TryAgain(countTries: Int) extends TwReaderCommand

  private val MAX_TRIES = 10
}
class TweetsReaderActor(
   val executor: ActorRef[ExecutorsActor.ExecutorCommand],
   val streamActor: ActorRef[StreamHandlerActor.StreamHandlerCommand],
 ) {

  import TweetsReaderActor._

  private def prepareStream(): StreamStructure = StreamStructureBuilder()
    .withTweets(Set("author_id", "id", "created_at", "geo"))
    .withExpansions(Set("geo.place_id"))
    .withPlace(Set("geo", "id", "name", "place_type"))
    .withBackfillMinutes(0)
    .build()
  private def createInputStream(rules: String) = TwitterInputStream(rules, prepareStream())
  private def tryReadTweet(reader: BufferedReader, queue: ActorRef[TweetQueueActor.TwQueueCommand]): Boolean = {
    val line = reader.readLine
    if (line != null && line.nonEmpty) {
      executor ! ExecutorsActor.ResetTime
      queue ! TweetQueueActor.Enqueue(line)
      true
    } else {
      false
    }
  }

  def apply(initialRules: String): Behavior[TwReaderCommand] = Behaviors.setup {
    context =>
      context.log.info("Starting")
      val queue = context.spawn(new TweetQueueActor().apply(streamActor), "TweetsQueueActor")
      var twStream = createInputStream(initialRules)
      var reader = new BufferedReader(new InputStreamReader(twStream.inputStream))

    Behaviors.receive { (context, message) =>
      message match {
        case StartReader  =>
          Behaviors.withTimers(timers => {
            timers.startTimerWithFixedDelay(QueueTweets, 100.millis, 100.millis)
            queue ! TweetQueueActor.StartQueue
            Behaviors.same
          })
        case UpdateReader(terms: String) =>
          endStream(twStream)
          reader.close()
          twStream = createInputStream(terms)
          reader = new BufferedReader(new InputStreamReader(twStream.inputStream))
          Behaviors.same
        case QueueTweets =>
          context.log.info("Queueing")
          if (!tryReadTweet(reader, queue)) {
            context.self ! TryAgain(0)
          }
          Behaviors.same
        case TryAgain(MAX_TRIES) =>
          context.log.error(s"Unsuccessful after $MAX_TRIES tries... Ending")
          context.self ! EndReader
          Behaviors.same
        case TryAgain(numOfTries) =>
          // continue trying again
          context.log.info("Trying again [{}]", numOfTries)
          if (!tryReadTweet(reader, queue)) {
            context.self ! TryAgain(numOfTries + 1)
          }
          Behaviors.same
        case EndReader =>
          context.log.info("Ending")
          if (reader != null) {
            endStream(twStream)
            reader.close()
            queue ! TweetQueueActor.Flush
          }
          Behaviors.stopped
      }
    }
  }
}
