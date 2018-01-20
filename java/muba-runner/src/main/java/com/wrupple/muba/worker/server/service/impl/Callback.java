package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import java.util.Arrays;
import java.util.Collection;

public class Callback<T extends Context> extends ChainBase<T> implements StateTransition<T> {


    public Callback() {
    }

    public Callback(Command command) {
        super(command);
    }

    public Callback(Command[] commands) {
        super(commands);
    }

    public Callback(Collection commands) {
        super(commands);
    }

    @Override
    public StateTransition<T> hook(StateTransition<T> hooked) {
        super.addCommand(hooked);
        return hooked;
    }

    @Override
    public Collection<StateTransition<T>> getChildren() {
        return (Collection) Arrays.asList(super.commands);
    }

}
