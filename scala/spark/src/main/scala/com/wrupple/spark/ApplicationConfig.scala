package com.wrupple.spark

import java.io.PrintWriter
import javax.inject.{Inject, Named}
import javax.sql.DataSource

import com.google.inject.{AbstractModule, Provides}
import com.wrupple.muba.bpm.server.service.{BusinessPlugin, SolverCatalogPlugin}
import com.wrupple.muba.catalogs.server.chain.command._
import com.wrupple.muba.catalogs.server.chain.command.impl._
import com.wrupple.muba.catalogs.server.service.{CatalogPlugin, SystemCatalogPlugin}
import org.apache.commons.dbutils.QueryRunner
import org.hsqldb.jdbc.JDBCDataSource

class ApplicationConfig extends AbstractModule {

  @Provides
  @Inject def queryRunner(ds: DataSource) = new QueryRunner(ds)

  @Provides
  @javax.inject.Inject
  @javax.inject.Singleton
  @Named("catalog.plugins") def plugins(bpm: BusinessPlugin, runner: SolverCatalogPlugin, system: SystemCatalogPlugin): Any = {
    val plugins = Array[CatalogPlugin](runner, bpm, system)
    plugins
  }

  override protected def configure(): Unit = {

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
