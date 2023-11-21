package twitter_connection
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.{AddOrDeleteRulesRequest, AddRulesRequest, RuleNoId}

import scala.jdk.CollectionConverters._

class StreamStructure(
                       tweets: Option[Set[String]] = None,
                       place: Option[Set[String]] = None,
                       expansions: Option[Set[String]] = None,
                       backfillMinutes: Option[Int] = None
                     ) {

  override def toString: String = {
    def _toString(optional: Option[Any]): String = optional match {
      case Some(contained) => contained match {
        case set: Set[_] => s"[${set.mkString(",")}]"
        case _ => s"[${contained.toString}]"
      }
      case None => ""
    }

    Seq(("tweets",tweets), ("place", place), ("expansions", expansions), ("backfillMinutes", backfillMinutes))
      .map(tuple => s"${tuple._1}:${_toString(tuple._2)}@")
      .foldLeft(new StringBuilder)((sb, str) => sb.append(str))
      .toString()
  }

  private def createAddRule(rules: String): AddOrDeleteRulesRequest = {
    val addOrDeleteRulesRequest = new AddOrDeleteRulesRequest()
    val addRuleRequest = new AddRulesRequest()
    val ruleRequest = new RuleNoId()
    ruleRequest.setValue(rules)
    addRuleRequest.addAddItem(ruleRequest)
    addOrDeleteRulesRequest.setActualInstance(addRuleRequest)
    addOrDeleteRulesRequest
  }

  def createStream(rules: String, twitterApi: twitter_api_mock.TwitterApi): TwitterInputStream = {
    val tweets = twitterApi.tweets()
    tweets.addOrDeleteRules(createAddRule(rules))
    new TwitterInputStream(tweets.searchStream().execute())
  }

  private def prepareParams(): List[(String, Any)] = {
    val tweetFields = Set("author_id", "id", "created_at", "geo")
    val expansions = Set("geo.place_id")
    val placeFields = Set("geo", "id", "name", "place_type")

    ("tweetFields", tweetFields) :: ("expansions", expansions) :: ("placeFields", placeFields) :: ("backfillMinutes", 0) :: Nil
  }
}

object StreamStructure {
  def parseStructure(strStructure: String): StreamStructure = {
    // tweets:[abc,def]@place:@expansions:@backfillMinutes:[0]@
    strStructure
      .split("@")
      .map(_.split(":"))
      .filter(kv => kv.length > 1)
      .foldLeft(StreamStructureBuilder())(
        (builder, kv) => {
          val key = kv(0)
          val value = kv(1).stripSuffix("]").stripPrefix("[")
          key match {
            case "tweets" => builder.withTweets(value.split(",").toSet)
            case "place" => builder.withPlace(value.split(",").toSet)
            case "expansions" => builder.withExpansions(value.split(",").toSet)
            case "backfillMinutes" => builder.withBackfillMinutes(value.toInt)
          }
      }).build()
  }
}

case class StreamStructureBuilder private (
                                            tweets: Option[Set[String]] = None,
                                            place: Option[Set[String]] = None,
                                            expansions: Option[Set[String]] = None,
                                            backfillMinutes: Option[Int] = None
                                          ) {
  def withTweets(tweets: Set[String]): StreamStructureBuilder =
    copy(tweets = Some(tweets))

  def withPlace(place: Set[String]): StreamStructureBuilder =
    copy(place = Some(place))

  def withExpansions(expansions: Set[String]): StreamStructureBuilder =
    copy(expansions = Some(expansions))

  def withBackfillMinutes(backfillMinutes: Int): StreamStructureBuilder =
    copy(backfillMinutes = Some(backfillMinutes))

  def build() = new StreamStructure(
    tweets = tweets,
    place = place,
    expansions = expansions,
    backfillMinutes = backfillMinutes
  )
}
