package com.wrupple.muba.catalogs.server.service.impl

import java.lang
import javax.inject.{Inject, Singleton}

import com.wrupple.muba.catalogs.domain.{CatalogActionContext, REGISTRY_CATALOG, SparkTableRegistry}
import com.wrupple.muba.catalogs.server.service.{SparkCatalogPlugin, TableDescriptorBuilder}
import com.wrupple.muba.event.domain.CatalogDescriptor
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SQLContext

import scala.collection.JavaConverters._


@Singleton
class SparkCatalogPluginImpl @Inject()(
                                        val sqlContext: SQLContext,

                                        val builder: TableDescriptorBuilder
                                      ) extends SparkCatalogPlugin with Logging {
  override def getDescriptor(aLong: lang.Long, catalogActionContext: CatalogActionContext) = ???

  override def getDescriptor(s: String, catalogActionContext: CatalogActionContext) = ???


  override def postProcessCatalogDescriptor(catalogDescriptor: CatalogDescriptor, catalogActionContext: CatalogActionContext) = ???

  override def getCatalogActions = ???

  override def getValidations = ???


  def setAvailableCatalogs(context: CatalogActionContext, catalogDescriptor: CatalogDescriptor) = {
    val filter = FilterDataUtils.newFilterData();
    filter.setConstrained(false);

    logger.info("assembling catalog descriptor of each table")

    val catalogs = context.
      triggerRead(REGISTRY_CATALOG, filter).
      asScala.
      map(a => builder.fromTable(context, sqlContext, a.asInstanceOf[SparkTableRegistry]))

    context.setResults(java.util.Arrays.asList(catalogs: _*))
  }
}
