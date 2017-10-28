package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.server.chain.PublishEvents;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class PublishEventsImpl    implements PublishEvents {


    private final StreamingDelegate delegate;
    protected static final Logger log = LoggerFactory.getLogger(PublishEventsImpl.class);

    @Inject
    public PublishEventsImpl(StreamingDelegate delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        BroadcastContext context = (BroadcastContext) ctx;
        BroadcastEvent queueElement=context.getEventValue();
        EventBus eventBus = context.getRuntimeContext().getEventBus();
        log.debug("<PublishEventsImpl>");
        log.debug("Broadcast event {}",queueElement);

        // UPDATE CLIENT'S ACTIVITY STATUS

        Collection<Host> concernedPeers = context.getConcernedPeersValues();
        if(concernedPeers==null){
            log.debug("no peer concerned in event");
        }else{
            log.debug("streaming event to concerned peers");
            delegate.streamToConcernedPeers(context, queueElement, eventBus, concernedPeers);
        }


        log.debug("</PublishEventsImpl>");

        return CONTINUE_PROCESSING;

    }

    public interface StreamingDelegate {
        void streamToConcernedPeers(BroadcastContext context, BroadcastEvent queueElement, EventBus eventBus, Collection<Host> concernedPeers) throws Exception;

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
