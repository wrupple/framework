package com.wrupple.muba.desktop.client.service;

import com.wrupple.muba.event.domain.reserved.HasChildren;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public interface StateTransition<T> extends HasChildren<StateTransition<T>>, Command {

    void setResult(T previousStateOutput, Context context);

    StateTransition<T> hook(StateTransition<T> hooked);

}
