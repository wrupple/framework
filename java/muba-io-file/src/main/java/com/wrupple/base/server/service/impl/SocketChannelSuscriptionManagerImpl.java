package com.wrupple.base.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.services.ObjectMapper;

public class SocketChannelSuscriptionManagerImpl extends AbstractSocketChannelSuscriptionManager implements SocketChannelSuscriptionManager {

	@Inject
	public SocketChannelSuscriptionManagerImpl( Provider<ObjectMapper> omp,
			Provider<BPMClientData> bpmclientData) {
		super( omp,  bpmclientData);
	}

	@Override
	public String getUsableSocketChannel(BPMClientData desktopSession, CatalogDataAccessObject<BPMClientData> dsm, CatalogExcecutionContext context) {
		return "";
	}


	@Override
	public void updateOrCreatePersistentSession(BPMClientData desktopSession) {

	}

	@Override
	protected BPMClientData getObjectById(String cookieToken) {
		//Long clientid = WriteAuditTrailsImpl.parseRememberMeToken(cookieToken);
		return null;
	}

	@Override
	protected List<BPMClientData> getReusableSocketCandidates(long userDomain) {
		return null;
	}

	@Override
	protected void generateAndSendMessages(String targetCatalogId, String targetAction, List<BPMClientData> concernedClients, Object firstResult,
			String targetEntryId) throws Exception {
		// 
	}

}
