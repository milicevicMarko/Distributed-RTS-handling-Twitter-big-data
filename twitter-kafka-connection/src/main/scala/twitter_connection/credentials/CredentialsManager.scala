package twitter_connection.credentials

import java.io.{FileInputStream, IOException}
import java.util.Properties

object CredentialsManager {
  @throws[IOException]
  private def fetchCredentials = try {
    val twitter_properties = sys.env.getOrElse("TWITTER_CREDENTIALS", "resources/twitter-credentials.properties")

    val fileInputStream = new FileInputStream(twitter_properties)
    try {
      val properties = new Properties
      properties.load(fileInputStream)
      println("Successfully imported credentials")
      properties
    } finally if (fileInputStream != null) fileInputStream.close()
  } catch {
    case e: IOException =>
      System.err.println("Error while fetching credentials")
      throw e
  }


  def getToken: String = fetchCredentials.getProperty("bearerToken")
}
