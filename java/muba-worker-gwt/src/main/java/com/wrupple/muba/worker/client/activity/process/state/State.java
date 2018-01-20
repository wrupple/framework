package com.wrupple.muba.worker.client.activity.process.state;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.worker.server.service.StateTransition;

/**
 * Changes or creates data from an input and notifies when it is done
 *
 * @author japi
 */
public interface State<I, O> {


    void start(final I parameter, final StateTransition<O> onDone, EventBus bus);

    interface ContextAware<I, O> extends State<I, O> {

        void setContext(ProcessContextServices context);

    }
}
