package spark

import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.expressions.Aggregator

class MapAggregator extends Aggregator[GenericRowWithSchema, Map[String, Int], String] {

  override def zero: Map[String, Int] = Map.empty

  override def reduce(b: Map[String, Int], row: GenericRowWithSchema): Map[String, Int] = {
    val mapFromRow = row.getAs[Map[String, Int]]("word_map")
    mapFromRow.foldLeft(b) { case (acc, (k, v)) =>
      acc + (k -> (acc.getOrElse(k, 0) + v))
    }
  }

  override def merge(b1: Map[String, Int], b2: Map[String, Int]): Map[String, Int] = {
    b1.foldLeft(b2) { case (acc, (k, v)) =>
      acc + (k -> (acc.getOrElse(k, 0) + v))
    }
  }

  override def finish(reduction: Map[String, Int]): String = reduction.toString()


  override def bufferEncoder: Encoder[Map[String, Int]] = Encoders.kryo[Map[String, Int]]

  override def outputEncoder: Encoder[String] = Encoders.STRING
}
