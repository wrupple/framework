package com.wrupple.muba.catalogs.domain

import com.wrupple.muba.event.domain.reserved.HasParent
import com.wrupple.muba.event.domain.{CatalogEntry, ContractDescriptor}

trait SparkTableRegistry extends CatalogEntry with ContractDescriptor with HasParent[Long] {

  def getAssignedCatalogKey(): Long

  def getSchema(): String
}
