package com.wrupple.vegetate.server.services;

import com.wrupple.muba.catalogs.domain.VegetatePeer;
import com.wrupple.muba.catalogs.server.domain.RequestScopedContext;

public interface PeerManager {
	 void renewLease(VegetatePeer desktopSession, RequestScopedContext context); 
}
