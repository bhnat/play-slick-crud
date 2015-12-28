package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json._
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class UserController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                               (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  /**
   * A REST endpoint that gets all the users as JSON.
   */
  def getAll = Action.async {
    repo.list().map { users =>
      Ok(Json.toJson(users))
    }
  }

  def getOnly(id: Long) = Action.async {
    repo.list(id).map { user =>
      Ok(Json.toJson(user))
    }
  }

  /**
   * A REST endpoint that creates a new user from JSON.
   */
  def create = LoggingAction.async(BodyParsers.parse.json) { request => 
    val user = request.body.validate[User]
    user.fold(
      errors => Future(BadRequest(Json.obj(
          "status" -> "Parsing user failed",
          "error" -> JsError.toJson(errors)))),
      user => 
        repo.create(user.firstName, user.lastName, user.authToken, user.email).map { u =>
            Ok(Json.obj("status" -> "Success", "id" -> u.id))
        }
    )
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { request =>
    val user = request.body.validate[User]
    user.fold(
      errors => Future(BadRequest(Json.obj(
          "status" -> "Parsing user failed",
          "error" -> JsError.toJson(errors)))),
      user => 
        repo.update(id, user).map { u =>
            Ok(Json.obj("status" -> "Success"))
        }
    )
  }

  def delete(id: Long) = Action.async {
    repo.delete(id).map { u => Ok(Json.obj("status" -> "Success")) }
  }

}
