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
      "content" -> nonEmptyText
    )(TalkForm.apply)(TalkForm.unapply)
  }

  def getMessages(dialogId: Long) = Action.async {
    repo.list(dialogId).map { dialog =>
      Ok(Json.toJson(dialog))
    }
  }

  def talk = Action.async { implicit request =>
    talkForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest)
      },
      talk => {
        repo.create(talk.dialogId, talk.username, talk.content, currentUTCDateString()).map(msg => {
          reply(msg)
          NoContent
        })
      }
    )
  }

  def teach = Action.async { implicit request =>
    talkForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest)
      },
      talk => {
        repo.create(talk.dialogId, "Yoyo", talk.content, currentUTCDateString()).map(msg => {
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
          repo.create(message.dialogId, "Yoyo", answers(index).content, currentUTCDateString())
        }
      }), Duration.Inf)
  }

  def toUTCDateString(date: Date) = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(date)
  def currentUTCDateString() = toUTCDateString(Calendar.getInstance().getTime)
}

case class TalkForm(dialogId: Long, username: String, content: String)
