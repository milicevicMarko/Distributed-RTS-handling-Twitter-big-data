package io_utils

import play.api.libs.json._

import java.io.FileInputStream
import scala.util.Random

object ImportJsonUtil {
  private def getFile: JsArray = {
    val filename = "twitter-dataset.json"
    // for local, remove "/app/" from path
    val docker_prefix = "/app/"
    val stream = new FileInputStream(docker_prefix + "src/main/resources/" + filename)
    val json = try {
      Json.parse(stream)("arr").as[JsArray]
    } finally {
      stream.close()
    }
    json
  }

  private val MOCKED_TWEETS: JsArray = ImportJsonUtil.getFile

  private val random = new Random()

  def getRandom = MOCKED_TWEETS.value(random.nextInt(MOCKED_TWEETS.value.length))
}
