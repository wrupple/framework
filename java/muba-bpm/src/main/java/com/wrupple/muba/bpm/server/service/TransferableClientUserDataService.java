package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.SessionContextDTO;

public interface TransferableClientUserDataService {

	SessionContextDTO getTransferableClientUserData(SessionContext session,long userDomain,long personId, BPMPeer peer);

}
