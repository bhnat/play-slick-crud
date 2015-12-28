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