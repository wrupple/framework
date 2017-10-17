package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.PublishEvents;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.stream.Stream;

@Singleton
public class PublishEventsImpl    implements PublishEvents {
    protected static final Logger log = LoggerFactory.getLogger(PublishEventsImpl.class);

    private final boolean parallel;
    private final Provider<BroadcastQueueAppend> queueProvider;

    @Inject
	public PublishEventsImpl(@Named("event.parallel") Boolean parallel,  Provider<BroadcastQueueAppend> queueProvider) {
		super();
		this.parallel = parallel.booleanValue();
        this.queueProvider = queueProvider;
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
            Stream<Host> s = parallel?concernedPeers.parallelStream():concernedPeers.stream();

            log.debug("streaming event to concerned peers");
            s.forEach((Host host) -> {
                BroadcastQueueAppend queue = queueProvider.get();
                queue.setHostValue(host);
                queue.setQueuedElementValue(queueElement);
                queue.setCatalog(queueElement.getEventValue().getCatalogType());
                try {
                    log.debug("Append to broadcast channel of host {}",host);
                    eventBus.fireEvent(queue, context.getRuntimeContext(), null);
                } catch (Exception e) {
                    log.error("failed to append event to host broadcast queue",e);
                }
                //return queue;
            });
        }


        log.debug("</PublishEventsImpl>");

        return CONTINUE_PROCESSING;

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
