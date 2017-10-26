package com.wrupple.spark

import com.google.inject.AbstractModule
import com.wrupple.muba.catalogs.server.chain.command.impl.{SparkQueryCommandImpl, SparkReadCommandImpl}
import com.wrupple.muba.catalogs.server.chain.command.{SparkQueryCommand, SparkReadCommand}

/**
  * https://github.com/codingwell/scala-guice
  * http://michaelpnash.github.io/guice-up-your-scala/
  */
class SparkModule extends AbstractModule {
  override def configure() = {
    //bind(classOf[JDBCDataCreationCommand]).to(classOf[JDBCDataCreationCommandImpl])
    bind(classOf[SparkQueryCommand]).to(classOf[SparkQueryCommandImpl])
    bind(classOf[SparkReadCommand]).to(classOf[SparkReadCommandImpl])
    /*
    bind(classOf[JDBCDataWritingCommand]).to(classOf[JDBCDataWritingCommandImpl])
    bind(classOf[JDBCDataDeleteCommand]).to(classOf[JDBCDataDeleteCommandImpl])
*/
  }
}
