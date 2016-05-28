package com.wrupple.muba.cms.server.services.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;

import com.wrupple.muba.catalogs.server.service.ContentManagementSystem;
import com.wrupple.muba.cms.server.chain.ContentManager;

@Singleton
public class ContentManagementSystemImpl extends CatalogBase implements ContentManagementSystem {

	private final ContentManager defaultOne;

	@Inject
	public ContentManagementSystemImpl(ContentManager defaultOne) {
		super();
		this.defaultOne = defaultOne;
	}


	@Override
	public Command getCommand(String name) {
		Command contentManager = super.getCommand(name);
		if (contentManager == null) {
			contentManager = defaultOne;
		}
		return contentManager;
	}

}
