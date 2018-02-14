package com.wrupple.batch

import org.apache.logging.log4j.scala.Logging
import org.scalatest.FunSpec
import org.scalatest.mockito.MockitoSugar

/**
  *
  */
class HiveTableDescriptorBuilderTest
    extends FunSpec
    with MockitoSugar
    with Logging {
  /*
    // (1) init
    val service = mock[DataTypeMapper]
    //val contextCaptor: ArgumentCaptor[CatalogActionContext] = ArgumentCaptor.forClass(classOf[CatalogActionContext])

    val context = mock[CatalogActionContext]

  http://blog.themillhousegroup.com/2013/11/using-mockitos-argumentcaptor-in-scala.html

  val sql = mock[SQLContext]
  val table = mock[SparkTableRegistry]

  val subject = new HiveTableDescriptorBuilder(service, "test");


  describe("A Hive table descriptor") {

    it("should pass a non-empty list of catalog descriptor items to the context") {
      val captor = ArgumentCaptor.forClass(classOf[List[String]])

      val result: CatalogDescriptor = subject.fromTable(context, sql, table)

      // TODO first item in context's result list should be === to result

      assert(result.getDistinguishedName.equals(table.getDistinguishedName))
      //TODO assert fields are right
    }
  }*/

}
