package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.SessionContext;
import org.apache.commons.chain.Context;

public interface ContextRewriter {

    void rewriteContext(Context context, ServiceManifest manifest, String serviceId, SessionContext usip);

}
