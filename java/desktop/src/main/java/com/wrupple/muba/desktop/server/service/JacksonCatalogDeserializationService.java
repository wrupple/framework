package com.wrupple.muba.desktop.server.service;

import org.apache.commons.chain.Context;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface JacksonCatalogDeserializationService extends CatalogDeserializationService {

	CatalogEntry deserialize(ObjectNode root, CatalogDescriptor descriptor, Context context) throws Exception;

}
