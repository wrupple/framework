package com.wrupple.muba.event.domain;

import java.util.Collection;

/**
 * Created by japi on 30/09/17.
 */
public interface BroadcastContext extends ServiceContext{

    final String CURRENT_EVENT="catalog.event.processing";
    final String CONCERNED_CLIENTS = "catalog.event.concerned";
    BroadcastEvent getEventValue();

    void setEventValue(BroadcastEvent contract);

    BroadcastContext setRuntimeContext(RuntimeContext requestContext);

    void addConcernedPeers(Collection<? extends Host> results);

    Collection<Host> getConcernedPeersValues();
}
