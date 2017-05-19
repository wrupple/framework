package com.wrupple.muba.desktop.server.service;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.services.SessionContext;

public interface ContextRewriter {

	void rewriteContext(Context context, VegetateServiceManifest manifest, String serviceId, SessionContext usip);

}
