package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.reserved.HasChildren;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public interface StateTransition<T extends Context> extends HasChildren<StateTransition<T>>, Command<T> {

    StateTransition<T> hook(StateTransition<T> hooked);

}
