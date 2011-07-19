package c2c.webspecs.suite

import org.specs2.SpecificationWithJUnit
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP1.ImportCheMetadataSpec
import c2c.webspecs.geonetwork.geocat.spec.WP3._

class AllSpecs extends SpecificationWithJUnit with SpecificationsFinder { def is =
    examplesLinks("All Work Packages")

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[WP1],
      classOf[WP3]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class WP3 extends SpecificationWithJUnit with SpecificationsFinder { def is =

    examplesLinks("WP 3: Shared Object (No UI)")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[AccessFormatsSpec],
        classOf[AccessContactsSpec],
        classOf[AccessExtentsSpec],
        classOf[AccessKeywordsSpec],
        classOf[AddSharedContactsSpec],
        classOf[AddSharedExtentsSpec],
        classOf[AddSharedKeywordsSpec],
        classOf[AddSharedFormatSpec],
        classOf[ProcessImportedMetadataSpec],
        classOf[ImportSpecialExtentsSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class WP1 extends SpecificationWithJUnit with SpecificationsFinder { def is =

    examplesLinks("WP 1: Add CHE Schema")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[ImportCheMetadataSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}