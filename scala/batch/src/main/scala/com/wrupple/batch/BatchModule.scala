package com.wrupple.batch

import java.io.PrintWriter
import javax.inject.Inject
import javax.sql.DataSource

import com.google.inject.{AbstractModule, Provides}
import com.wrupple.batch.service.impl.{BatchJobInterpretImpl, BatchJobRunnerImpl, BatchModelResolverImpl, SparkMessageDelegate}
import com.wrupple.batch.service.{BatchJobInterpret, BatchJobRunner, BatchMessageDelegate, BatchModelResolver}
import com.wrupple.muba.catalogs.server.chain.command._
import com.wrupple.muba.catalogs.server.chain.command.impl._
import org.apache.commons.dbutils.QueryRunner
import org.hsqldb.jdbc.JDBCDataSource

class BatchModule extends AbstractModule {

  @Provides
  @Inject def queryRunner(ds: DataSource) = new QueryRunner(ds)

  override protected def configure(): Unit = {
    bind(classOf[BatchJobRunner]).to(classOf[BatchJobRunnerImpl])
    bind(classOf[BatchJobInterpret]).to(classOf[BatchJobInterpretImpl])
    bind(classOf[BatchModelResolver]).to(classOf[BatchModelResolverImpl])
    bind(classOf[BatchMessageDelegate]).to(classOf[SparkMessageDelegate])


    // this makes JDBC the default storage unit
    bind(classOf[DataCreationCommand]).to(classOf[JDBCDataCreationCommandImpl])
    bind(classOf[DataQueryCommand]).to(classOf[JDBCDataQueryCommandImpl])
    bind(classOf[DataReadCommand]).to(classOf[JDBCDataReadCommandImpl])
    bind(classOf[DataWritingCommand]).to(classOf[JDBCDataWritingCommandImpl])
    bind(classOf[DataDeleteCommand]).to(classOf[JDBCDataDeleteCommandImpl])

    val ds = new JDBCDataSource
    ds.setLogWriter(new PrintWriter(System.err))
    ds.setPassword("")
    ds.setUser("SA")
    ds.setUrl("jdbc:hsqldb:mem:aname")

    bind(classOf[DataSource]).toInstance(ds)
    /*
     * COMMANDS
     */

  }

}
