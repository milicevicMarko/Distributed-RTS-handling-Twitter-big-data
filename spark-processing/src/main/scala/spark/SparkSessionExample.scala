package spark

import kafka.connection_properties.DefaultConnectionProperties
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{SparkSession, functions}

import scala.collection.mutable


object SparkSessionExample {

  private case class JoinedDF(
                       sender: String,
                       message: mutable.WrappedArray[String],
                       terms: mutable.WrappedArray[String],
                       dataTime: Timestamp,
                       structureTime: Timestamp)

  private def readFromKafka(topic: String)(implicit spark: SparkSession) =
    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", DefaultConnectionProperties.SERVER)
      .option("subscribe", topic)
      .option("startingOffsets", "latest")
      .option("failOnDataLoss", "false")
      .load()

  def main(args: Array[String]): Unit = {
    val master = sys.env.getOrElse("SPARK_MASTER_CONTEXT", "local[2]")
    val outputMode = sys.env.getOrElse("SPARK_OUTPUT_MODE", "console")

    implicit val spark: SparkSession = SparkSession
      .builder()
      .config("spark.cleaner.referenceTracking.cleanCheckpoints", "true")
      .appName("SparkSessionExample")
      .master(master)
      .getOrCreate()

    import spark.implicits._

    val dataWatermark = sys.env.getOrElse("WATERMARK_DATA", "15 seconds")

    val dataStream = readFromKafka(DefaultConnectionProperties.DATA_TOPIC)
      .select(
        col("key").cast("STRING").as("sender"),
        col("value").cast("STRING").as("message"))
      .withColumn("message",
        functions.split(
          functions.lower(
            functions.regexp_replace($"message", "[,.!?:;]", " ")), "\\s+"))
      .withColumn("dataTime", current_timestamp())
      .withWatermark("dataTime", dataWatermark)

    val structureWatermark = sys.env.getOrElse("WATERMARK_STRUCTURE", "15 seconds")
    val structureStream = readFromKafka(DefaultConnectionProperties.STRUCTURE_TOPIC)
      .select(
        col("key").cast("STRING").as("structureSender"),
        col("value").cast("STRING").as("structure"))
      .withColumn("terms", functions.split(functions.regexp_replace($"structure", "[\\(\\)]", ""), " OR "))
      .drop("structure")
      .withColumn("structureTime", current_timestamp())
      .withWatermark("structureTime", structureWatermark)

    val interval = "5 minutes"
    val windowJoin = "dataTime BETWEEN structureTime AND structureTime + INTERVAL " + interval
    val senderJoin = "sender = structureSender"
    val joinExpr = expr(senderJoin + " AND " + windowJoin)
    val joinedStream = structureStream
      .join(dataStream, joinExpr)
      .drop("structureSender")
      .as[JoinedDF]

    def filterFunction(messages: mutable.WrappedArray[String], terms: mutable.WrappedArray[String]) = {
      messages.filter(terms.contains(_))
    }
    val udfFilterFunction = udf(filterFunction(_: mutable.WrappedArray[String], _: mutable.WrappedArray[String]))

    def mapOccurrences(filteredMessages: mutable.WrappedArray[String]) = {
      filteredMessages.groupBy(identity).mapValues(_.size)
    }
    val udfMapOccurrences = udf(mapOccurrences(_: mutable.WrappedArray[String]))

    val filteredStream = joinedStream
      .withColumn("word_map", udfMapOccurrences(udfFilterFunction($"message", $"terms")))

    filteredStream.printSchema()
    // root
    // |-- sender: string (nullable = true)
    // |-- message: array (nullable = true)
    // |    |-- element: string (containsNull = true)
    // |-- dataTime: timestamp (nullable = false)
    // |-- terms: array (nullable = true)
    // |    |-- element: string (containsNull = true)
    // |-- structureTime: timestamp (nullable = false)
    // |-- word_map: map (nullable = true)
    // |    |-- key: string
    // |    |-- value: integer (valueContainsNull = false)

    val interim = filteredStream
      .select( $"sender", $"word_map", $"dataTime")

    val resultDataWatermark = sys.env.getOrElse("WATERMARK_RESULT", "15 seconds")
    val windowDuration = sys.env.getOrElse("WINDOW_DURATION", "15 seconds")
    val slideDuration = sys.env.getOrElse("WINDOW_SLIDE", "15 seconds")

    val aggregator = new MapAggregator().toColumn.as("agg_result")
    val result = interim
      .withWatermark("dataTime", resultDataWatermark)
      .groupBy(window($"dataTime", windowDuration, slideDuration), $"sender")
      .agg(aggregator)

    result.printSchema()

    val processingTime = sys.env.getOrElse("PROCESSING_TIME", "15 seconds")
    println("Output mode: " + outputMode)
    if (outputMode == "kafka") {
        val kafkaOutput = result
          .select(col("sender").as("key"), col("agg_result").as("value"))
          .writeStream
          .outputMode(OutputMode.Append())
          .format("kafka")
          .option("kafka.bootstrap.servers", DefaultConnectionProperties.SERVER)
          .option("topic", DefaultConnectionProperties.RESULT_TOPIC)
          .option("checkpointLocation", "checkpoint") // possible s
          .option("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
          .option("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
          .trigger(Trigger.ProcessingTime(processingTime))
          .start()
        kafkaOutput.awaitTermination()
    } else {
      val consoleOutput = result
        .writeStream
        .outputMode(OutputMode.Append())
        .format("console")
        .option("truncate", value = false)
        .trigger(Trigger.ProcessingTime(processingTime))
        .start()
      consoleOutput.awaitTermination()
    }
  }
}
