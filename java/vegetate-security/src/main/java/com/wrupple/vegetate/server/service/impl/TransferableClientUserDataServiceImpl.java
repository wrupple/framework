package com.wrupple.vegetate.server.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.SessionContextDTOImpl;
import com.wrupple.muba.bpm.server.service.TransferableClientUserDataService;
import com.wrupple.muba.catalogs.server.services.SessionContext;

@Singleton
public class TransferableClientUserDataServiceImpl implements TransferableClientUserDataService {

	
	
	
	@Inject
	public TransferableClientUserDataServiceImpl() {
		super();
	}


	@Override
	public SessionContextDTO getTransferableClientUserData(SessionContext session, long userDomain, long personId, BPMPeer peer) {
		
		SessionContextDTOImpl r = new SessionContextDTOImpl(session.getUserPrincipal(), String.valueOf(userDomain), String.valueOf(personId), peer);

		return r;
	}

}
