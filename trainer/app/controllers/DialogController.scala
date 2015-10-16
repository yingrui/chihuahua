package controllers

import javax.inject._

import dal._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class DialogController @Inject()(repo: DialogRepository, messageRepo: MessageRepository, val messagesApi: MessagesApi)
                                (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val dialogForm: Form[CreateDialogForm] = Form {
    mapping(
      "topic" -> nonEmptyText,
      "relationship" -> nonEmptyText,
      "username" -> nonEmptyText
    )(CreateDialogForm.apply)(CreateDialogForm.unapply)
  }

  def index = Action {
    Ok(views.html.index())
  }

  def dialog(dialogId: Long) = Action.async {
    repo.getById(dialogId).map(dialog =>
      Ok(Json.toJson(dialog))
    )
  }

  def createDialog = Action.async { implicit request =>
    dialogForm.bindFromRequest.fold(
      (errorForm: Form[CreateDialogForm]) => {
        Future.successful(Ok(views.html.index()))
      },
      dialog => {
        repo.create(dialog.topic, dialog.relationship, dialog.username).map { savedDialog =>
          Created.withHeaders(("Location", s"/dialogs/${savedDialog.id}"))
        }
      }
    )
  }

  def getDialogs = Action.async {
    repo.list().map { dialogs =>
      Ok(Json.toJson(dialogs))
    }
  }
}

case class CreateDialogForm(topic: String, relationship: String, username: String)