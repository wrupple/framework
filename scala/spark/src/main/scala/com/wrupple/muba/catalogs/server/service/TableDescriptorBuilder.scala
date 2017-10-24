package com.wrupple.muba.catalogs.server.service

import com.wrupple.muba.catalogs.domain.{CatalogActionContext, SparkTableRegistry}
import com.wrupple.muba.event.domain.CatalogDescriptor
import org.apache.spark.sql.SQLContext

trait TableDescriptorBuilder {
  def fromTable(context: CatalogActionContext, sqLContext: SQLContext, tableMetadata: SparkTableRegistry): CatalogDescriptor


}
