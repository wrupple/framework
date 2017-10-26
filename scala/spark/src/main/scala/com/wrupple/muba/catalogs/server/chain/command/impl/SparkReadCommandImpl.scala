package com.wrupple.muba.catalogs.server.chain.command.impl

import javax.inject.Inject

import com.wrupple.muba.catalogs.domain.CatalogActionContext
import com.wrupple.muba.catalogs.domain.impl.{SparkLazyEntities, SparkLazyResults}
import com.wrupple.muba.catalogs.server.chain.command.SparkReadCommand
import com.wrupple.muba.catalogs.server.service.{SQLDelegate, TableMapper}
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues
import org.apache.commons.chain.{Command, Context}
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SQLContext

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

    val dfAll = sqlContext.sql(builder.toString);


    val dfUnique = dfAll.filter(dfAll(keyField) === key);

    if (catalogDescriptor.getClazz.equals(classOf[HasAccesablePropertyValues])) {

      val results = new SparkLazyEntities(dfUnique, catalogDescriptor, tableMapper)
      context.setResults(results);
    } else {

      val results = new SparkLazyResults(dfUnique, catalogDescriptor, tableMapper)
      context.setResults(results);
    }





    return Command.CONTINUE_PROCESSING;
  }
}