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

import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class MocaController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                               (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  def test = Action.async {

    val url = "?"
    val cmd = 
      <moca-request autocommit="True">
        <environment>
        </environment>
        <query>
          login user where usr_id = '?' and usr_pswd = '?'
        </query>
      </moca-request>
    // val contentLength = cmd.toString().length.toString()  
    WS.url(url)
      .withHeaders("Content-Type" -> "application/moca-xml", "Response-Encoder" -> "xml", "Connection" -> "Keep-Alive")
      .post(cmd)
      .map { response =>
      	Logger.info(response.body)
      	Logger.info(response.status.toString())
      	val resultSet = MocaResultSet.fromXml(response.xml)
        Ok(resultSet.asJson) //Ok(response.xml \\ "field")
    }
  }

}