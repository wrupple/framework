package com.wrupple.muba.catalogs

import com.google.inject.AbstractModule
import com.wrupple.muba.catalogs.server.chain.command.SparkQueryCommand
import com.wrupple.muba.catalogs.server.chain.command.impl.SparkQueryCommandImpl

/**
  * https://github.com/codingwell/scala-guice
  * http://michaelpnash.github.io/guice-up-your-scala/
  */
class SparkModule extends AbstractModule {
  override def configure() = {
    //bind(classOf[JDBCDataCreationCommand]).to(classOf[JDBCDataCreationCommandImpl])
    bind(classOf[SparkQueryCommand]).to(classOf[SparkQueryCommandImpl])
    /*bind(classOf[JDBCDataReadCommand]).to(classOf[JDBCDataReadCommandImpl])
    bind(classOf[JDBCDataWritingCommand]).to(classOf[JDBCDataWritingCommandImpl])
    bind(classOf[JDBCDataDeleteCommand]).to(classOf[JDBCDataDeleteCommandImpl])

    bind(classOf[JDBCMappingDelegate]).to(classOf[JDBCMappingDelegateImpl])
    bind(classOf[QueryResultHandler]).to(classOf[QueryResultHandlerImpl])
*/
  }
}
