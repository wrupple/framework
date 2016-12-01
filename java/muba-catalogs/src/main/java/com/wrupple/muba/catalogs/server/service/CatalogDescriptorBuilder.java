package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

public interface CatalogDescriptorBuilder {

	public  CatalogDescriptor fromClass(Class<? extends CatalogEntry> clazz, String catalogId, String catalogName, long numericId, CatalogDescriptor parent);
}
