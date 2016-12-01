package com.wrupple.muba.bpm.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.server.service.ContentManagementSystem;
import com.wrupple.muba.bootstrap.server.service.impl.Dictionary;
import com.wrupple.muba.bpm.server.chain.ContentManager;

@Singleton
public class ContentManagementSystemImpl extends Dictionary implements ContentManagementSystem {


	@Inject
	public ContentManagementSystemImpl(CatalogFactory factory,ContentManager defaultOne) {
		super(defaultOne);
		factory.addCatalog(CatalogActionRequest.CATALOG_FIELD, this);
	}


}
