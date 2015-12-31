package models

import play.api.libs.json._

case class MocaColumn(name:String, nullable:Boolean, dataType:String) //nullable="true" length="0" type="S" name=

object MocaColumn {
  
  implicit val mocaColumnFormat = Json.format[MocaColumn]

  // def fromXml(node: scala.xml.Node):MocaResultSet = {
  // }
}

class MocaResultSet(status:Int, columns:List[MocaColumn], rows:List[List[String]]) {

  def row(i:Int): List[String] = {
	rows(i)
  }

  def rowAsJson(i:Int): JsValue = {
  	Json.toJson(rows(i))
  }

  def asJson: JsValue = {
  	Json.toJson(rows.map(columns.map(_.name).zip(_).toMap))
  }

  // TODO: handle invalid parameters
  def valueAt(row:Int, column:Int): String = {
  	rows(row)(column)
  }

  // TODO: handle invalid parameters
  def valueFor(row:Int, column:String): String = {
  	val index = columns.indexOf(column)
  	rows(row)(index)
  }
}

object MocaResultSet {
  def fromXml(node: scala.xml.Node):MocaResultSet = {
  	val status = (node \\ "status").text.toInt
  	val columns = (node \\ "column").map( n => 
  	  MocaColumn(n.attributes("name").toString, n.attributes("nullable").toString.toBoolean, n.attributes("type").toString) ).toList
  	val rows = for { row <- (node \\ "row") } yield (for { field <- (row \\ "field") } yield field.text).toList
    new MocaResultSet(status, columns, rows.toList)
  }
}