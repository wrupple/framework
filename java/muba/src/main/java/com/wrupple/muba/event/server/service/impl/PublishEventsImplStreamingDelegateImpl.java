package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.BroadcastQueueAppend;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class PublishEventsImplStreamingDelegateImpl implements PublishEventsImpl.StreamingDelegate {
    private final Provider<BroadcastQueueAppend> queueProvider;

    @Inject
    public PublishEventsImplStreamingDelegateImpl(Provider<BroadcastQueueAppend> queueProvider) {
        this.queueProvider = queueProvider;
    }

    @Override
    public void streamToConcernedPeers(BroadcastContext context, BroadcastEvent queueElement, EventBus eventBus, Collection<Host> concernedPeers) throws Exception {
        for (Host host : concernedPeers) {
            BroadcastQueueAppend queue = queueProvider.get();
            queue.setHostValue(host);
            queue.setQueuedElementValue(queueElement);
            queue.setCatalog(queueElement.getEventValue().getCatalogType());
            eventBus.fireEvent(queue, context.getRuntimeContext(), null);

        }
    }
}
