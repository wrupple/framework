package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.stream.Stream;

@Singleton
public class StreamingDelegateImpl implements PublishEventsImpl.StreamingDelegate {
    protected static final Logger log = LogManager.getLogger(StreamingDelegateImpl.class);

    private final boolean parallel;

    private final Provider<RemoteBroadcast> queueProvider;

    @Inject
    public StreamingDelegateImpl(@Named("event.parallel") Boolean parallel, Provider<RemoteBroadcast> queueProvider) {
        this.parallel = parallel;
        this.queueProvider = queueProvider;
    }

    @Override
    public void streamToConcernedPeers(BroadcastContext context, BroadcastEvent queueElement, ServiceBus serviceBus, Collection<Host> concernedPeers) {
        Stream<Host> s = parallel ? concernedPeers.parallelStream() : concernedPeers.stream();

        s.forEach((Host host) -> {
            RemoteBroadcast queue = queueProvider.get();
            queue.setHostValue(host);
            queue.setQueuedElementValue(queueElement);
            queue.setCatalog(queueElement.getEventValue().getCatalogType());
            queue.setDomain(queueElement.getDomain());
            try {
                log.debug("Append to broadcast channel of host {}", host);
                serviceBus.fireEvent(queue, context.getRuntimeContext(), null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //return queue;
        });
    }
}
