package twitter_connection

import com.twitter.clientlib.TwitterCredentialsBearer
import com.twitter.clientlib.api.TwitterApi
import twitter_connection.credentials.CredentialsManager

import java.io.InputStream

case class TwitterInputStream(inputStream: InputStream)

object TwitterInputStream {
  private lazy val twitterApi: twitter_api_mock.TwitterApi = createTwitterApi(CredentialsManager.getToken)

  private def createTwitterApi(token: String): twitter_api_mock.TwitterApi = new twitter_api_mock.TwitterApi(new TwitterCredentialsBearer(token))

  def apply(rules: String, streamStructure: StreamStructure): TwitterInputStream = streamStructure.createStream(rules, twitterApi)

  def endStream(inputStream: TwitterInputStream): Unit = inputStream.inputStream.close()

  // create a whole new supervisor
//  def updateParams(terms: Array[String], streamStructure: StreamStructure) = streamStructure.upsertRules(twitterApi, terms.mkString(" OR "))
}
