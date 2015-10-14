package dal

import javax.inject.{Inject, Singleton}

import models.Dialog
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DialogRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class DialogTable(tag: Tag) extends Table[Dialog](tag, "dialogs") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def topic = column[String]("topic")
    def relationship = column[String]("relationship")
    def username = column[String]("username")

    def * = (id, topic, relationship, username) <> ((Dialog.apply _).tupled, Dialog.unapply)
  }

  private val dialogs = TableQuery[DialogTable]

  def create(topic: String, relationship: String, username: String): Future[Dialog] = db.run {
    dialogs.map(dialog => (dialog.topic, dialog.relationship, dialog.username))
      .returning(dialogs.map(_.id))
      .into(((data, id) => Dialog(id, data._1, data._2, data._3))) += (topic, relationship, username)
  }

  def getById(id: Long): Future[Dialog] = db.run {
    dialogs.filter(_.id === id).result.head
  }

  def list(): Future[Seq[Dialog]] = db.run {
    dialogs.result
  }
}
