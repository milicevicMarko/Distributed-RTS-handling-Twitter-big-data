package twitter_connection

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.twitter.clientlib.model.{ConnectionExceptionProblem, OperationalDisconnectProblem, StreamingTweetResponse}
import kafka.kafka_producer.KafkaProducerActor

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object StreamHandlerActor {
  sealed trait StreamHandlerCommand

  case class ProcessStreamingObject(tweetString: String) extends StreamHandlerCommand
}

class StreamHandlerActor(
                          val executorActor: ActorRef[ExecutorsActor.ExecutorCommand],
                          val kafkaProducer: ActorRef[KafkaProducerActor.SendTweet]
) {
  import StreamHandlerActor._

  private def processStreamingObject(tweetStream: String): Unit = extractData(getStreamingObject(tweetStream))

  private def getStreamingObject(tweetString: String): StreamingTweetResponse = try {
    validateStream(StreamingTweetResponse.fromJson(tweetString))
  } catch {
    case e: Exception =>
      executorActor ! ExecutorsActor.Error(s"Unexpected Error: ${e.getMessage}")
      StreamingTweetResponse.fromJson(tweetString)
  }

  private def validateStream(streamingTweet: StreamingTweetResponse): StreamingTweetResponse = {
    if (streamingTweet.getErrors != null) {
      streamingTweet.getErrors.foreach {
        case _: OperationalDisconnectProblem | _: ConnectionExceptionProblem =>
          val errorMessage = s"Re-connecting to the stream due to: ${streamingTweet.getErrors}"
          executorActor ! ExecutorsActor.Error(s"Error: streamingTweet.getErrors not null $errorMessage")
          executorActor ! ExecutorsActor.Reconnect
      }
    }
    streamingTweet
  }

  private def extractData(streamingTweet: StreamingTweetResponse): Unit = {
    if (streamingTweet.getData == null) {
      executorActor ! ExecutorsActor.Error(s"Error: streamingTweet.getData is null")
      executorActor ! ExecutorsActor.Reconnect
    } else {
      kafkaProducer ! KafkaProducerActor.SendTweet(streamingTweet.getData.getText)
    }
  }

  def apply(): Behavior[StreamHandlerCommand] = Behaviors.receive { (_, message) =>
    message match {
      case ProcessStreamingObject(tweetString) =>
        processStreamingObject(tweetString)
        Behaviors.same
    }
  }
}
