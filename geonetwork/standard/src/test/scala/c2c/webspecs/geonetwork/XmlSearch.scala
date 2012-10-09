package c2c.webspecs
package geonetwork

import xml.NodeSeq

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:23 PM
 */

case class XmlSearch(maxRecords:Int, params:(Any, Any)*)
  extends AbstractGetRequest("q", XmlSearchResultFactory,
    (params ++ Seq('to -> maxRecords, 'fast -> 'index, 'hitsperpage -> maxRecords)).map(Param.stringMapping):_*) {
  
  def sortBy(field:Any, ascending:Boolean) = {
    val newParams = {
      val basicSort = Map(params:_*).updated('sortBy, field)
      if(ascending) basicSort.updated('sortOrder, 'reverse)
      else basicSort
    }
    
    XmlSearch(maxRecords, newParams.toSeq:_*)
  }
  
  def from(idx: Int) = {
    val newParams = Map(params: _*).updated('from, idx)
    XmlSearch(maxRecords, newParams.toSeq: _*)
  }

  def to(idx: Int) = {
    val newParams = Map(params: _*).updated('to, idx)
    XmlSearch(maxRecords, newParams.toSeq: _*)
  }
}

object XmlSearchResultFactory extends BasicValueFactory[XmlSearchValues] {
  def createValue(rawValue: BasicHttpValue) = new XmlSearchValues(rawValue)
}

case class XmlSearchValues(rawValue:BasicHttpValue) {
  lazy val xml = XmlValueFactory.createValue(rawValue).getXml
  lazy val records = (xml \ "metadata").toList map (new XmlSearchValue(_))
  lazy val summary = (xml \ "summary").headOption
  lazy val size = (summary.flatMap (_.attribute("count").map(_.text)).headOption getOrElse "0").toInt
  lazy val to = (xml.flatMap (_.attribute("to").map(_.text)).headOption getOrElse "0").toInt
  lazy val from = (xml.flatMap (_.attribute("from").map(_.text)).headOption getOrElse "0").toInt
}
class XmlSearchValue(xml:NodeSeq) {
  override def toString = title
  val title = recordValue("defaultTitle")

  def recordValue(name: String) = (xml \ name).text
  def infoValue(name: String) = (xml \ "info" \ name).text
}