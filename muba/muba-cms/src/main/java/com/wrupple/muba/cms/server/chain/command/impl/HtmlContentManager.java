package com.wrupple.muba.cms.server.chain.command.impl;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.cms.server.services.ContentManagerManifest;

@Singleton
public class HtmlContentManager  extends WriteFormatedDocumentImpl {

	@Inject
	public HtmlContentManager( ContentManagerManifest cmsm, CatalogPropertyAccesor accessor, @Named("cms.tokenRegEx")String pattern) {
		super("UTF-8","text/html", cmsm, accessor, pattern);
	}


}
