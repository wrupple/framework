package com.wrupple.muba.worker.client.services.impl;

import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.ArrayList;
import java.util.Collection;


public abstract class DataCallback<T> implements StateTransition<T> {
    protected T result;
    private ArrayList<StateTransition<T>> hooks;

    public static <T> DataCallback<T> nullCallback() {
        return new DataCallback<T>() {

            @Override
            public void execute() {

            }

        };
    }

    @Override
    public void setResult(T result) {
        this.result = result;
        if (hooks != null) {
            for (StateTransition<T> callback : hooks) {
                callback.setResult(result);
                callback.execute();
            }
        }
    }

    @Override
    public StateTransition<T> hook(StateTransition<T> callback) {
        if (this == callback) {
            //the programer is just beeing an asshole

        } else {
            if (hooks == null) {
                hooks = new ArrayList<StateTransition<T>>(3);
            }
            hooks.add(callback);
        }
        return this;

    }

    @Override
    public Collection<StateTransition<T>> getChildren() {
        return hooks;
    }

    @Override
    public void setResultAndFinish(T result) {
        setResult(result);
        execute();
    }

}
