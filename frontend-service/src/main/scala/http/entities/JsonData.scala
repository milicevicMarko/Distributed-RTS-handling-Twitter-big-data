package http.entities

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat, enrichAny}

import scala.language.implicitConversions

trait JsonData
final case class SessionData(sessionId: String) extends JsonData
final case class StructureData(sessionId: String, terms: Array[String]) extends JsonData

trait JsonDataSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val sessionDataFormat: RootJsonFormat[SessionData] = jsonFormat1(SessionData.apply)
  implicit val structureDataFormat: RootJsonFormat[StructureData] = jsonFormat2(StructureData.apply)

  def toJson(jsonData: JsonData): JsValue = jsonData match {
    case sessionData: SessionData => sessionData.toJson
    case structureData: StructureData => structureData.toJson
  }
}
