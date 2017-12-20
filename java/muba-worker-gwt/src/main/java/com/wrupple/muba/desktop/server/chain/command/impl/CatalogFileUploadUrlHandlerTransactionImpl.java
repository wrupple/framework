package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.UploadURlResponsetHandler;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.desktop.domain.WruppleImageMetadata;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class CatalogFileUploadUrlHandlerTransactionImpl implements CatalogFileUploadUrlHandlerTransaction {

	private CatalogServiceManifest manifest;

	@Inject
	public CatalogFileUploadUrlHandlerTransactionImpl(CatalogServiceManifest manifest) {
		super();
		this.manifest = manifest;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		CatalogActionRequest request = new CatalogActionRequestImpl(String.valueOf(context.getDomain()), WruppleImageMetadata.CATALOG,
				CatalogActionRequest.UPLOAD_ACTION, null, null, null, null);
		List<String> urls = Collections.singletonList(manifest.buildServiceRequestUri('/', request));
		context.put(UploadURlResponsetHandler.UPLOAD_URLS, urls);
		return CONTINUE_PROCESSING;
	}

}
