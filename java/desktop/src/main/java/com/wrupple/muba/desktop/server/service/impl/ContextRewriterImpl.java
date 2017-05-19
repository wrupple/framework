package com.wrupple.muba.desktop.server.service.impl;

import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.desktop.server.service.ContextRewriter;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.services.SessionContext;

@Singleton
public class ContextRewriterImpl implements ContextRewriter {

	@Override
	public void rewriteContext(Context context, VegetateServiceManifest manifest, String serviceId, SessionContext usip) {
		if (CatalogServiceManifest.SERVICE_NAME.equals(manifest.getServiceName())) {
		}
	}

}
