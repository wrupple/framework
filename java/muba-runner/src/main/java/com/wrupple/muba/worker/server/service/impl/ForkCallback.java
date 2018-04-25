package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;


public class ForkCallback<T extends Context> extends Callback<T> {


    private int finished;

    public ForkCallback(StateTransition<T> finalCallback) {
        finished = 1;
        super.addCommand(finalCallback);
    }

    public synchronized StateTransition<T> fork() {
        return synchronizedFork(new Callback<T>() {
            @Override
            public boolean execute(T context) throws Exception {
                boolean retorno = super.execute(context);
                if (isFinished()) {
                    return doexecute(context);
                }
                return retorno;
            }
        });
    }

    private boolean doexecute(T context) throws Exception {
        return super.execute(context);
    }

    protected synchronized StateTransition<T> synchronizedFork(Command command) {
        super.addCommand(command);
        return (StateTransition<T>) command;
    }

    private synchronized boolean isFinished() {
        finished++;
        return super.commands.length == finished;
    }


}
