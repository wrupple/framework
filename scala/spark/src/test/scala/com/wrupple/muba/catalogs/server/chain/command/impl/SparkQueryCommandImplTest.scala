package com.wrupple.muba.catalogs.server.chain.command.impl

import com.wrupple.muba.catalogs.domain.CatalogActionContext
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl
import com.wrupple.muba.catalogs.server.service.TableMapper
import com.wrupple.muba.catalogs.server.service.impl.{FilterDataUtils, SQLDelegateImpl}
import org.apache.commons.chain.Command
import org.apache.spark.sql.SQLContext
import org.mockito.ArgumentCaptor
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSuite, OneInstancePerTest, _}


//@RunWith(classOf[JUnitRunner])
class SparkQueryCommandImplTest extends FunSuite with MockitoSugar with Matchers with OneInstancePerTest {

  val sql = mock[SQLContext]
  val tableMapper = mock[TableMapper]
  val subject = new SparkQueryCommandImpl(tableMapper, new SQLDelegateImpl('\"'), sql);

  test("Spark Query assigns a non empty list of entries to context result") {
    val captor = ArgumentCaptor.forClass(classOf[CatalogActionContext])
    val context = mock[CatalogActionContext]
    val filter = FilterDataUtils.newFilterData()
    val contract = new CatalogActionRequestImpl()
    contract.setFilter(filter);
    /*
    import org.mockito.Mockito._
        //when(functionProviderMock.addingFunction(1)).thenReturn((i: Int) ⇒ i + 1)

        Spark is friendly to unit testing with any popular unit test framework. Simply create a SparkContext in your test with the master URL set to local, run your operations, and then call SparkContext.stop() to tear it down. Make sure you stop the context within a finally block or the test framework’s tearDown method, as Spark does not support two contexts running concurrently in the same program.

     */
    //doReturn(contract).when(context).getRequest
    //expect context.setResults() to receive a non empty list

    //Matchers
    subject.execute(context) should be(Command.CONTINUE_PROCESSING)


  }

}
