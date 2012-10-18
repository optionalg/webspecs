package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class PagingBasicSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractPagingSearchSpec[XmlSearchValues] {

  def page(startRecord: Int) = {
    val records = XmlSearch().range(startRecord, startRecord+1).search('abstract -> (time + "NonSpatialSearchQuerySpec")).sortBy("date", false).execute().value

    new {
      val codes = findCodesFromResults(records)
      val nextRecord = records.to + 1
      val recordsReturned = records.size
      val totalHits = records.count
    }
  }
  override def minimumRecordReturned = 1
}