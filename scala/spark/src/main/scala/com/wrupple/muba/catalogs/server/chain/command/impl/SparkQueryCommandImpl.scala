package com.wrupple.muba.catalogs.server.chain.command.impl

import javax.inject.{Inject, Singleton}

import com.wrupple.muba.catalogs.domain.CatalogActionContext
import com.wrupple.muba.catalogs.server.chain.command.SparkQueryCommand
import com.wrupple.muba.catalogs.server.service.SparkCatalogPlugin
import org.apache.commons.chain.Context
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SQLContext

@Singleton
class SparkQueryCommandImpl @Inject()(
                                       val sqlContext: SQLContext,

                                       val plugin: SparkCatalogPlugin
                                     ) extends SparkQueryCommand with Logging {

  override def execute(ctx: Context): Boolean = {

    val context = ctx.asInstanceOf[CatalogActionContext]
    val catalogDescriptor = context.getCatalogDescriptor
    val filter = context.getRequest.getFilter




    // return Command.CONTINUE_PROCESSING;
  }


}
