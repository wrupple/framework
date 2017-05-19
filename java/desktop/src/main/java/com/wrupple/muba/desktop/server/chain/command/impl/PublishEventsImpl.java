package com.wrupple.muba.desktop.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.server.service.BusinessEventSuscriptionManager;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;

public class PublishEventsImpl implements PublishEvents {

	private final BusinessEventSuscriptionManager socketManager;
	// set client statis to online
	private BusinessEventSuscriptionManager bdsm;

	@Inject
	public PublishEventsImpl(BusinessEventSuscriptionManager socketManager, BusinessEventSuscriptionManager bdsm) {
		super();
		this.socketManager = socketManager;
		this.bdsm = bdsm;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		System.err.println("[Publish Request Events]");

		// UPDATE CLIENT'S ACTIVITY STATUS

		String remembermeToken = context.getPeer();

		if (remembermeToken != null) {
			BPMPeer client = socketManager.getPeerByEncodedId(remembermeToken);
			if (client != null) {
				if (client.getSubscriptionStatus() != null && client.getSubscriptionStatus().intValue() != BPMPeer.STATUS_ONLINE) {
					client.setSubscriptionStatus(BPMPeer.STATUS_ONLINE);

					socketManager.applyChanges(client);
				}
			}
		}

		// BROADCAST EVENT'S TO CONCERNED CLIENTS

		List<CatalogBroadcastData> broadcastable = (List<CatalogBroadcastData>) context.get(CatalogBroadcastData.class.getName());
		if (broadcastable != null && !broadcastable.isEmpty()) {
			// FIXME the concerned clients probaly have more to do with the
			// domain of the data modified (think of published app scenario)
			List<BPMPeer> concernedClients;
			for (CatalogBroadcastData data : broadcastable) {

				// push to online clients
				concernedClients = bdsm.getConcernedClients(data, data.getDomain());
				if (concernedClients != null) {
					socketManager.publishImplicitEvents(data, concernedClients);
				}

			}
		}

		return CONTINUE_PROCESSING;
	}

}
