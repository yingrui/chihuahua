package controllers

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import javax.inject._

import dal._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

class MessageController @Inject()(repo: MessageRepository, val messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val talkForm: Form[TalkForm] = Form {
    mapping(
      "dialogId" -> longNumber,
      "username" -> nonEmptyText,
      "content" -> nonEmptyText,
      "tags" -> default(text, "")
    )(TalkForm.apply)(TalkForm.unapply)
  }

  def getMessages(dialogId: Long) = Action.async {
    repo.list(dialogId).map { dialog =>
      Ok(Json.toJson(dialog))
    }
  }

  def message(messageId: Long) = Action.async {
    repo.getById(messageId).map(message =>
      Ok(Json.toJson(message))
    )
  }

  def updateMessage(messageId: Long) = Action.async { implicit request =>
    talkForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest)
      },
      talk => {
        repo.update(messageId, talk.content, talk.tags).map(_ =>
          NoContent
        )
      }
    )
  }

  def deleteMessage(messageId: Long) = Action.async {
    repo.delete(messageId).map(_ => NoContent)
  }

  def createMessage = Action.async { implicit request =>
    talkForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest)
      },
      talk => {
        repo.create(talk.dialogId, talk.username, talk.content, currentUTCDateString(), 0, talk.tags).map(msg => {
          reply(msg)
          Created.withHeaders(("Location", s"/messages/${msg.id}"))
        })
      }
    )
  }

  def teach(questionId: Long) = Action.async { implicit request =>
    talkForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest)
      },
      talk => {
        val message = Await.result(repo.getById(questionId), Duration.Inf)
        repo.create(talk.dialogId, talk.username, talk.content, toUTCDateString(add(message.getTime, 1)), questionId, talk.tags).map(msg => {
          NoContent
        })
      }
    )
  }

  def reply(message: Message): Unit = {
    Await.result(
      repo.search(message.content).map(matchedMessages => {
        val answers = matchedMessages.filter(_.id != message.id).map(_.id + 1).map(id => Await.result(repo.getById(id), Duration.Inf)).filter(_.username != message.username)
        if (!answers.isEmpty) {
          val index = new Random(Calendar.getInstance().getTimeInMillis).nextInt(answers.size)
          repo.create(message.dialogId, "system", answers(index).content, currentUTCDateString(), message.id, "")
        }
      }), Duration.Inf)
  }

  def toUTCDateString(date: Date) = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(date)
  def add(date: Date, second: Int) = new Date(date.getTime + second * 1000L)
  def currentUTCDateString() = toUTCDateString(Calendar.getInstance().getTime)
}

case class TalkForm(dialogId: Long, username: String, content: String, tags: String)
