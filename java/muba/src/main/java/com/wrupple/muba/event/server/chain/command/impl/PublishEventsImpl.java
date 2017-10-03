package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class PublishEventsImpl extends LookupCommand   implements PublishEvents {

	public static boolean ENABLE_IMPLICIT_SUSCRIPTIONS = true;

	@Inject
	public PublishEventsImpl(CatalogFactory factory) {
		super(factory);
		super.setNameKey(PublishEvents.CHANNEL_DICTIONARY);
		super.setCatalogName(PublishEvents.CHANNEL_DICTIONARY);
	}




    @Override
    public boolean execute(Context ctx) throws Exception {
        BroadcastContext context = (BroadcastContext) ctx;
        EventBroadcastQueueElement queueElement=context.getEventValue();
        List<FilterCriteria> explicitObservers = queueElement.getObserversValues();
        Event event = queueElement.getEventValue();


        System.err.println("[Publish Request Events]");

        // UPDATE CLIENT'S ACTIVITY STATUS

        Collection<Host> concernedClients = context.getConcernedPeers();


        //FIXME a hosts channel determines what broadcast queue to place the event in

        Host client = (Host) context.getRuntimeContext().getSession().getPeerValue();

        // String remembermeToken = peer.getPublicKey();

        if (client != null) {
            if (client.getSubscriptionStatus() != null
                    && client.getSubscriptionStatus().intValue() != Host.STATUS_ONLINE) {
                client.setSubscriptionStatus(Host.STATUS_ONLINE);

            }
        }

        List<CatalogEvent> broadcastable = context.getRootAncestor().getEvents();

        // BROADCAST EVENT'S TO CONCERNED CLIENTS

        if (broadcastable != null && !broadcastable.isEmpty()) {
            Collection<Host> concernedClients;
            for (CatalogEvent data : broadcastable) {

                // push to online clients
                concernedClients = getConcernedClients(data, context);
                Session session = context.getCatalogManager().access().newSession(null);
                if (concernedClients != null) {
                    publishImplicitEvents(data, concernedClients,context,session);
                }

            }
        }

        return CONTINUE_PROCESSING;

    }



	private void publishImplicitEvents(CatalogEvent event, Collection<Host> concernedClients,CatalogActionContext context, Session session) throws Exception {
		boolean goAhead = actionRequiresNotifying(event.getName());
		if (goAhead && concernedClients!=null  &&! concernedClients.isEmpty() ){
			for(Host client : concernedClients){
				context.put(EventSuscriptionChain.CONCERNED_CLIENTS, client);
				context.put(EventSuscriptionChain.CURRENT_EVENT, event);
				context.put(CHANNEL_DICTIONARY, context.getCatalogManager().getDenormalizedFieldValue(client,Host.CHANNEL_FIELD, session, context));
				super.execute(context);
			}
		}
	}

	
	/*
	 * CONFIGURE DESKTOP RESPONSE
	 *
	protected String attemptToReuseExistingChannel(Host desktopSession, CatalogDataAccessObject<Host> dsm,
			CatalogActionContext context) throws Exception {

		List<Host> reusableCandidates = getReusableSocketCandidates(context.getDomain());
		if (reusableCandidates == null || reusableCandidates.isEmpty()) {
			return null;
		} else {
			boolean isUsable;
			String newsocketChannel = null;
			for (Host offlineSession : reusableCandidates) {
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

	

	private boolean assertSocketIsUsable(Host desktopSession, CatalogDataAccessObject<Host> dsm,
			CatalogActionContext context) {

	}

	protected abstract List<Host> getReusableSocketCandidates(long userDomain);

	*/

}
