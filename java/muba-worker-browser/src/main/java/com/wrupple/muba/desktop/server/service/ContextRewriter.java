package com.wrupple.muba.desktop.server.service;

import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.services.SessionContext;
import org.apache.commons.chain.Context;

public interface ContextRewriter {

    void rewriteContext(Context context, VegetateServiceManifest manifest, String serviceId, SessionContext usip);

}
