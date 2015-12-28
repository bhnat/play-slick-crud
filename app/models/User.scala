package models

import play.api.libs.json._

case class User(id: Long, firstName: String, lastName: String, authToken: String, email:String)

object User {
  
  implicit val personFormat = Json.format[User]
}