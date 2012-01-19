package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.spec._


@RunWith(classOf[JUnitRunner])
class AllSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[CSW]
	).flatMap{s => createSpecification(s.getName)}
      specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
    }

    def initVal(t:String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000))

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
}


class CSW extends Specification with SpecificationsFinder { def is =

    examplesLinks("WP 1: Add CHE Schema")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[csw.CswTransactionUpdateSpec],
        classOf[csw.CswGetCapabilitiesServiceUrlSpec],
        classOf[csw.CswLanguageSpec],
        classOf[csw.CswOutputSchemaSpec],
        classOf[csw.CswGetRecordsSpec],
        classOf[csw.CswGetRecordsByIdSpec]

      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

