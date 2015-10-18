package dal

import javax.inject.{Inject, Singleton}

import models.Message
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MessageRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class MessageTable(tag: Tag) extends Table[Message](tag, "messages") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def dialogId = column[Long]("dialog_id")
    def username = column[String]("username")
    def content = column[String]("content")
    def timestamp = column[String]("timestamp")
    def questionId = column[Long]("questionId")
    def tags = column[String]("tags")

    def * = (id, dialogId, username, content, timestamp, questionId, tags) <> ((Message.apply _).tupled, Message.unapply)
  }

  private val messages = TableQuery[MessageTable]

  def create(dialogId: Long, username: String, content:String, timestamp: String, questionId: Long, tags: String): Future[Message] = db.run {
    (messages.map(msg => (msg.dialogId, msg.username, msg.content, msg.timestamp, msg.questionId, msg.tags))
      returning messages.map(_.id)
      into ((data, id) => Message(id, data._1, data._2, data._3, data._4, data._5, data._6))
    ) += (dialogId, username, content, timestamp, questionId, tags)
  }

  def list(dialogId: Long): Future[Seq[Message]] = db.run {
    messages.filter(_.dialogId === dialogId).result
  }

  def search(content: String): Future[Seq[Message]] = db.run {
    messages.filter(_.content === content).result
  }

  def getById(id: Long): Future[Message] = db.run {
    messages.filter(_.id === id).result.head
  }

  def update(id: Long, content: String, tags: String): Future[Int] = db.run {
    messages.filter(_.id === id).map(msg => (msg.content, msg.tags)).update((content, tags))
  }

  def delete(id: Long): Future[Int] = db.run {
    messages.filter(_.id === id).delete
  }

  def deleteMessages(dialogId: Long): Future[Int] = db.run {
    messages.filter(_.dialogId === dialogId).delete
  }
}
