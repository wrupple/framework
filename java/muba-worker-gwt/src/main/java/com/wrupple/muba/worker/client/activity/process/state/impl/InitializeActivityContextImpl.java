package com.wrupple.muba.worker.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.dictionary.ExternalAPILoaderMap;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.state.InitializeActivityContext;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;

public class InitializeActivityContextImpl implements InitializeActivityContext {


    private JsApplicationItem item;
    private ExternalAPILoaderMap apiMap;
    //private boolean[] resolved;
    private ProcessContextServices context;

    @Inject
    public InitializeActivityContextImpl(ExternalAPILoaderMap apiMap) {
        super();
        this.apiMap = apiMap;
    }

    @Override
    public void start(DesktopPlace parameter, StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {

        JsTransactionApplicationContext nueva = JsTransactionApplicationContext.createObject().cast();
        nueva.setApplicationItem(item);

        JavaScriptObject appProperties = item.getPropertiesObject();
        String rawAPINames = GWTUtils.getAttribute(appProperties, ExternalAPILoaderMap.ATTRIBUTE);
        if (rawAPINames == null) {
            onDone.setResultAndFinish(nueva);
        } else {
            State<JsTransactionApplicationContext, JsTransactionApplicationContext>[] states = getAPILoadingStates(rawAPINames, appProperties, bus, nueva);
            start(nueva, onDone, bus, states);
        }

    }

    private State<JsTransactionApplicationContext, JsTransactionApplicationContext>[] getAPILoadingStates(String rawAPINames, JavaScriptObject appProperties,
                                                                                                          EventBus bus, JsTransactionApplicationContext nueva) {
        String[] names = rawAPINames.split(",");
        State<JsTransactionApplicationContext, JsTransactionApplicationContext>[] regreso = new State[names.length];
        State<JsTransactionApplicationContext, JsTransactionApplicationContext> temp;
        String name;
        for (int i = 0; i < names.length; i++) {
            name = names[i];
            GWTUtils.setAttribute(appProperties, ExternalAPILoaderMap.ATTRIBUTE, name);
            temp = apiMap.getConfigured(appProperties, context, bus, nueva);
            if (temp == null) {
                throw new IllegalArgumentException("unrecognized API name " + name);
            }
            regreso[i] = temp;
        }
        return regreso;
    }

    public void start(JsTransactionApplicationContext parameter, StateTransition<JsTransactionApplicationContext> onDone, EventBus bus,
                      State<JsTransactionApplicationContext, JsTransactionApplicationContext>[] states) {
        if (this.resolved != null) {
            throw new IllegalArgumentException();
        }
        this.resolved = new boolean[states.length];
        State<JsTransactionApplicationContext, JsTransactionApplicationContext> state;
        for (int i = 0; i < states.length; i++) {
            state = states[i];
            state.start(parameter, new Resolver(i, onDone), bus);
        }
    }

    @Override
    public void setApplicationItem(JsApplicationItem item) {
        this.item = item;
    }

    @Override
    public void setContext(ProcessContextServices context) {
        this.context = context;
    }

    class Resolver extends DataCallback<JsTransactionApplicationContext> {
        final int index;
        final StateTransition<JsTransactionApplicationContext> onDone;

        public Resolver(int index, StateTransition<JsTransactionApplicationContext> onDone) {
            this.index = index;
            this.onDone = onDone;
        }

        @Override
        public void execute() {
            resolved[index] = true;
            for (boolean b : resolved) {
                if (!b) {
                    return;
                }
            }
            onDone.setResultAndFinish(result);
        }

    }

}
