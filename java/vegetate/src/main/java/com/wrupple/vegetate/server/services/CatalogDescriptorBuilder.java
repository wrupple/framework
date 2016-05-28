package com.wrupple.vegetate.server.services;

import com.wrupple.vegetate.domain.CatalogDescriptor;

public interface CatalogDescriptorBuilder {

	public  CatalogDescriptor fromClass(Class<?> clazz, String catalogId, String catalogName, long numericId);
}
