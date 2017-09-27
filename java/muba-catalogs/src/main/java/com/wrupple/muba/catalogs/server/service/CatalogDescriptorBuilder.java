package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogDescriptor;

public interface CatalogDescriptorBuilder {

	public <T extends CatalogEntry>  CatalogDescriptor fromClass(Class<T> clazz, String catalogId, String catalogName, long numericId, CatalogDescriptor parent) throws RuntimeException;
}
