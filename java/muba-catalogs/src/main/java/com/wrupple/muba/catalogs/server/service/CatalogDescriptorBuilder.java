package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

import java.lang.reflect.InvocationTargetException;

public interface CatalogDescriptorBuilder {

	public <T extends CatalogEntry>  CatalogDescriptor fromClass(Class<T> clazz, String catalogId, String catalogName, long numericId, CatalogDescriptor parent) throws RuntimeException;
}
