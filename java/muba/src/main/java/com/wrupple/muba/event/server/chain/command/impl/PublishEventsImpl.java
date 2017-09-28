package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogChangeEvent;
import com.wrupple.muba.event.server.chain.EventSuscriptionChain;
import com.wrupple.muba.event.server.chain.PublishEvents;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Singleton
public class PublishEventsImpl  extends LookupCommand   implements PublishEvents {
	
	private final EventSuscriptionChain chain;
	public static boolean ENABLE_IMPLICIT_SUSCRIPTIONS = true;

	@Inject
	public PublishEventsImpl(EventSuscriptionChain a,CatalogFactory factory) {
		super(factory);
		this.chain = a;
		super.setNameKey(PublishEvents.CHANNEL_DICTIONARY);
		super.setCatalogName(PublishEvents.CHANNEL_DICTIONARY);
	}
	
		
		


	// Object eval(CatalogActionContext context, String... sentence);
	@Override
	public boolean execute(Context c) throws Exception {
		/*CatalogActionContext context = (CatalogActionContext) c;
		System.err.println("[Publish Request Events]");

		// UPDATE CLIENT'S ACTIVITY STATUS

		CatalogPeer client = (CatalogPeer) context.getRuntimeContext().getSession().getPeerValue();

		// String remembermeToken = peer.getPublicKey();

		if (client != null) {
			if (client.getSubscriptionStatus() != null
					&& client.getSubscriptionStatus().intValue() != CatalogPeer.STATUS_ONLINE) {
				client.setSubscriptionStatus(CatalogPeer.STATUS_ONLINE);

			}
		}

		List<CatalogChangeEvent> broadcastable = context.getRootAncestor().getEvents();

		// BROADCAST EVENT'S TO CONCERNED CLIENTS

		if (broadcastable != null && !broadcastable.isEmpty()) {
			Collection<CatalogPeer> concernedClients;
			for (CatalogChangeEvent data : broadcastable) {

				// push to online clients
				concernedClients = getConcernedClients(data, context);
                Session session = context.getCatalogManager().access().newSession(null);
                if (concernedClients != null) {
					publishImplicitEvents(data, concernedClients,context,session);
				}

			}
		}
*/
		return CONTINUE_PROCESSING;
	}
/*
	private Collection<CatalogPeer> getConcernedClients(CatalogChangeEvent event, CatalogActionContext context) throws Exception {

		context.put(EventSuscriptionChain.CONCERNED_CLIENTS, new HashSet<CatalogPeer>());
		context.put(EventSuscriptionChain.CURRENT_EVENT, event);
		
		chain.execute(context);

		return (Collection<CatalogPeer>) context.get(EventSuscriptionChain.CONCERNED_CLIENTS);
	}


	private void publishImplicitEvents(CatalogChangeEvent event, Collection<CatalogPeer> concernedClients,CatalogActionContext context, Session session) throws Exception {
		boolean goAhead = actionRequiresNotifying(event.getName());
		if (goAhead && concernedClients!=null  &&! concernedClients.isEmpty() ){
			for(CatalogPeer client : concernedClients){
				context.put(EventSuscriptionChain.CONCERNED_CLIENTS, client);
				context.put(EventSuscriptionChain.CURRENT_EVENT, event);
				context.put(CHANNEL_DICTIONARY, context.getCatalogManager().getDenormalizedFieldValue(client,CatalogPeer.CHANNEL_FIELD, session, context));
				super.execute(context);
			}
		}
	}

	
	private boolean actionRequiresNotifying(String targetAction) {
		return CatalogActionRequest.WRITE_ACTION.equals(targetAction)
				|| CatalogActionRequest.CREATE_ACTION.equals(targetAction)
				|| CatalogActionRequest.DELETE_ACTION.equals(targetAction);
	}
	
	/*
	 * CONFIGURE DESKTOP RESPONSE
	 *
	protected String attemptToReuseExistingChannel(CatalogPeer desktopSession, CatalogDataAccessObject<CatalogPeer> dsm,
			CatalogActionContext context) throws Exception {

		List<CatalogPeer> reusableCandidates = getReusableSocketCandidates(context.getDomain());
		if (reusableCandidates == null || reusableCandidates.isEmpty()) {
			return null;
		} else {
			boolean isUsable;
			String newsocketChannel = null;
			for (CatalogPeer offlineSession : reusableCandidates) {
				isUsable = checkCachedChannelUsability(offlineSession);

				if (isUsable) {
					newsocketChannel = offlineSession.getPublicKey();
					Date newexpirationDate = offlineSession.getExpirationDate();

					offlineSession.setPublicKey(null);
					offlineSession.setExpirationDate(null);
					applyChanges(offlineSession);
					desktopSession.setPublicKey(newsocketChannel);
					desktopSession.setExpirationDate(newexpirationDate);
					break;
				} else {
					// all reusable channels have an unexpired status and
					// unexpired date of channel expiration
					newsocketChannel = null;
				}

			}
			if (newsocketChannel == null) {
				return null;
			} else {
				return newsocketChannel;
			}

		}
	}

	

	private boolean assertSocketIsUsable(CatalogPeer desktopSession, CatalogDataAccessObject<CatalogPeer> dsm,
			CatalogActionContext context) {

	}

	protected abstract List<CatalogPeer> getReusableSocketCandidates(long userDomain);

	*/

}
