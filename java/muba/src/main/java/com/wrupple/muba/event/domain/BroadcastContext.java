package com.wrupple.muba.event.domain;

import java.util.Collection;

/**
 * Created by japi on 30/09/17.
 */
public interface BroadcastContext extends ServiceContext{

    BroadcastEvent getEventValue();

    void setEventValue(BroadcastEvent contract);

    void addConcernedPeers(Collection<? extends Host> results);

    Collection<Host> getConcernedPeersValues();

}
