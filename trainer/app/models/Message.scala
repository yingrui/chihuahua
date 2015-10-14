package models

import play.api.libs.json._

case class Message(id: Long, dialogId: Long, username: String, content: String, timestamp: String)

object Message {
  implicit val messageFormat = Json.format[Message]
}
