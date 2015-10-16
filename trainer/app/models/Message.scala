package models

import play.api.libs.json._

case class Message(id: Long, dialogId: Long, username: String, content: String, timestamp: String, questionId: Long, tags: String) {
  def getTime = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(timestamp)
}

object Message {
  implicit val messageFormat = Json.format[Message]
}
