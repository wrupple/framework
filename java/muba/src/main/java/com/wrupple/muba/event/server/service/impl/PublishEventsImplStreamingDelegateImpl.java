package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class PublishEventsImplStreamingDelegateImpl implements PublishEventsImpl.StreamingDelegate {
    private final Provider<RemoteBroadcast> queueProvider;

    @Inject
    public PublishEventsImplStreamingDelegateImpl(Provider<RemoteBroadcast> queueProvider) {
        this.queueProvider = queueProvider;
    }

    @Override
    public void streamToConcernedPeers(BroadcastContext context, BroadcastEvent queueElement, ServiceBus serviceBus, Collection<Host> concernedPeers) throws Exception {
        for (Host host : concernedPeers) {
            RemoteBroadcast queue = queueProvider.get();
            queue.setHostValue(host);
            queue.setQueuedElementValue(queueElement);
            queue.setCatalog(queueElement.getEventValue().getCatalogType());
            serviceBus.fireEvent(queue, context.getRuntimeContext(), null);

        }
    }
}
