package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.SessionContextDTO;
import com.wrupple.vegetate.server.services.SessionContext;

public interface TransferableClientUserDataService {

	SessionContextDTO getTransferableClientUserData(SessionContext session,long userDomain,long personId, BPMPeer peer);

}
