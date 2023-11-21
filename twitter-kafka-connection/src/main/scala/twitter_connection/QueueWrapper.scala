package twitter_connection

import scala.collection.immutable.Queue

class QueueWrapper(var queue: Queue[String] = Queue.empty) {
  def poll: Option[String] = {
    if (isEmpty) None
    else {
      val (head, tail) = queue.dequeue
      queue = tail
      Some(head)
    }
  }

  def enqueue(streamingTweet: String): Unit = {
    queue = queue.enqueue(streamingTweet)
  }

  def isEmpty: Boolean = queue.isEmpty

  def size: Int = queue.size

  def flush(): Unit = queue = Queue.empty
}
