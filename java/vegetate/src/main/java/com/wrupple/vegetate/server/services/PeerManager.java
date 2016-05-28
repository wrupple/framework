package com.wrupple.vegetate.server.services;

import com.wrupple.vegetate.domain.VegetatePeer;

public interface PeerManager {
	 void renewLease(VegetatePeer desktopSession, RequestScopedContext context); 
}
