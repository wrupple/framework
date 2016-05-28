package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.server.services.ErrorAccuser;

public interface BPMPeerErrorAccuser extends ErrorAccuser<CatalogActionRequest> {

	void setPeer(BPMPeer peer);


}
