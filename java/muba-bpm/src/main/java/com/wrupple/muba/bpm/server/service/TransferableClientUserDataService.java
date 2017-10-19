package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.bpm.domain.BPMPeer;

public interface TransferableClientUserDataService {

	SessionContextDTO getTransferableClientUserData(SessionContext session,long userDomain,long personId, BPMPeer peer);

}
