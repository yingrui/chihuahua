package controllers

import java.text.SimpleDateFormat
import java.util.{Date, Calendar}

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

import javax.inject._

class DialogController @Inject()(repo: DialogRepository, messageRepo: MessageRepository, val messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val dialogForm: Form[CreateDialogForm] = Form {
    mapping(
      "topic" -> nonEmptyText,
      "relationship" -> nonEmptyText,
      "username" -> nonEmptyText
    )(CreateDialogForm.apply)(CreateDialogForm.unapply)
  }

  val talkForm: Form[TalkForm] = Form {
    mapping(
      "dialogId" -> longNumber,
      "username" -> nonEmptyText,
      "content" -> nonEmptyText
    )(TalkForm.apply)(TalkForm.unapply)
  }

  def index = Action {
    Ok(views.html.index(dialogForm))
  }

  def dialog(dialogId: Long) = Action.async {
    repo.getById(dialogId).map(dialog =>
      Ok(views.html.dialog(talkForm)(dialog))
    )
  }

  def createDialog = Action.async { implicit request =>
    dialogForm.bindFromRequest.fold(
      (errorForm: Form[CreateDialogForm]) => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      // There were no errors in the from, so create the person.
      dialog => {
        repo.create(dialog.topic, dialog.relationship, dialog.username).map { savedDialog =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.DialogController.dialog(savedDialog.id))
        }
      }
    )
  }

  def getDialogs = Action.async {
    repo.list().map { dialog =>
      Ok(Json.toJson(dialog))
    }
  }
}

case class CreateDialogForm(topic: String, relationship: String, username: String)