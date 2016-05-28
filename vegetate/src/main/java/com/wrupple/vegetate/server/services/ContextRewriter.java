package com.wrupple.vegetate.server.services;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.VegetateServiceManifest;

public interface ContextRewriter {

	void rewriteContext(Context context, VegetateServiceManifest manifest, String serviceId, SessionContext usip);

}
