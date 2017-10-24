package com.wrupple.muba.catalogs.server.chain.command.impl

import javax.inject.Inject

import com.wrupple.muba.catalogs.domain.{CONTEXT_RESULTFRAME, CatalogActionContext}
import com.wrupple.muba.catalogs.server.chain.command.SparkReadCommand
import com.wrupple.muba.catalogs.server.service.{SQLDelegate, TableMapper}
import org.apache.commons.chain.{Command, Context}
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.{Row, SQLContext}

import scala.collection.JavaConverters._

class SparkReadCommandImpl @Inject()(val tableMapper: TableMapper, val delegate: SQLDelegate,
                                     val sqlContext: SQLContext
                                    ) extends SparkReadCommand with Logging {

  override def execute(ctx: Context): Boolean = {

    val context = ctx.asInstanceOf[CatalogActionContext]
    val catalogDescriptor = context.getCatalogDescriptor
    val key = context.getRequest.getEntry
    val keyField = catalogDescriptor.getKeyField;
    val builder = new java.lang.StringBuilder(200);
    delegate.buildQueryHeader(tableMapper, builder, 0, catalogDescriptor, context, null);

    val dfResults = sqlContext.sql(builder.toString);

    context.asScala.put(CONTEXT_RESULTFRAME, dfResults);

    val rusultRow: Row = dfResults.filter(dfResults(keyField) === key).first();


    //FIXME
    //like jdbc result mapper but dynamically maps rows to objects using catalog reflection on the driver (requires collect anyway)

    context.setResult(result)

    return Command.CONTINUE_PROCESSING;
  }
}
