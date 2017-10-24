package com.wrupple.muba.catalogs.server.chain.command.impl

import javax.inject.{Inject, Provider, Singleton}

import com.wrupple.muba.catalogs.domain.{CatalogActionContext, _}
import com.wrupple.muba.catalogs.server.chain.command.SparkQueryCommand
import com.wrupple.muba.catalogs.server.domain.LazyList
import com.wrupple.muba.catalogs.server.service.{SQLDelegate, TableMapper}
import org.apache.commons.chain.{Command, Context}
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SQLContext

import scala.collection.JavaConverters._


@Singleton
class SparkQueryCommandImpl @Inject()(val resultProvider: Provider[LazyList], val tableMapper: TableMapper, val delegate: SQLDelegate,
                                      val sqlContext: SQLContext
                                     ) extends SparkQueryCommand with Logging {

  override def execute(ctx: Context): Boolean = {

    val context = ctx.asInstanceOf[CatalogActionContext]
    val catalogDescriptor = context.getCatalogDescriptor
    val filter = context.getRequest.getFilter

    val builder = new java.lang.StringBuilder(200);
    delegate.buildQueryHeader(tableMapper, builder, 0, catalogDescriptor, context, null);

    val dfResults = sqlContext.sql(builder.toString);

    context.asScala.put(CONTEXT_RESULTFRAME, dfResults);

    //FIXME filters

    val results = resultProvider.get();
    //results.setResolver(this);
    results.setSubject(dfResults);

    return Command.CONTINUE_PROCESSING;
  }


}
