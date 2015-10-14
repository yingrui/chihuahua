package models

import play.api.libs.json._

case class Dialog(id: Long, topic: String, relationship: String, username: String)

object Dialog {
  implicit val dialogFormat = Json.format[Dialog]
}
