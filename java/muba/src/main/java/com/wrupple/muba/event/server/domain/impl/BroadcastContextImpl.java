package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.impl.ContextBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BroadcastContextImpl extends ContextBase implements BroadcastContext {

    private BroadcastEvent eventValue;
    private RuntimeContext runtimeContext;
    private List<Host> concernedPeersValues;

    @Override
    public BroadcastEvent getEventValue() {
        return eventValue;
    }

    public void setEventValue(BroadcastEvent eventValue) {
        this.eventValue = eventValue;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;

    }

    @Override
    public void addConcernedPeers(Collection<? extends Host> results) {
        List<Host> list = getConcernedPeersValues();
        if(list==null){
            list = new ArrayList<>(2);
            setConcernedPeersValues(list);
        }
        list.addAll(results);
    }

    @Override
    public List<Host> getConcernedPeersValues() {
        return concernedPeersValues;
    }

    public void setConcernedPeersValues(List<Host> concernedPeersValues) {
        this.concernedPeersValues = concernedPeersValues;
    }
}
