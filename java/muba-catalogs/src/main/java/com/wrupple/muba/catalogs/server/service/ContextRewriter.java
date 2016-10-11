package com.wrupple.muba.catalogs.server.service;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.SessionContext;

public interface ContextRewriter {

	void rewriteContext(Context context, ServiceManifest manifest, String serviceId, SessionContext usip);

}
