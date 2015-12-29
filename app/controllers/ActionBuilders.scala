package controllers

import play.api._
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

object LoggingAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    Logger.info("Calling action")
    block(request)
  }
}

class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)

object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]) = Future.successful {
    new UserRequest(request.headers.get("sessionkey"), request)
  }
}

/** For actions requiring admin privileges */

object AdminCheckAction extends ActionFilter[UserRequest] {
  
  def filter[A](request: UserRequest[A]) = Future.successful {
    var sessionkey = request.headers.get("sessionkey").getOrElse("")
    if (sessionkey != "1234")
      Some(Results.Forbidden)
    else
      None
  }
}
