package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;

public interface PersistentCatalogEntityDAO extends
		PropertyMapDAO<PersistentCatalogEntity> {

	final static String FORCE_WRITE = "catalog.entityDao.forceWrite";

	PersistentCatalogEntity forceWriteAllFields(PersistentCatalogEntity targetEntity);
	
}
