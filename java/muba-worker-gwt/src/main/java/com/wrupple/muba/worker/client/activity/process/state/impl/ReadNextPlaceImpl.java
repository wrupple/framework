package com.wrupple.muba.worker.client.activity.process.state.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.dictionary.OutputHandlerMap;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.state.ReadNextPlace;
import com.wrupple.muba.worker.server.service.StateTransition;

public class ReadNextPlaceImpl implements ReadNextPlace {

    private ServiceBus serviceBus;
    private ProcessContextServices context;
    private JsApplicationItem applicationItem;
    private OutputHandlerMap outputmap;

    @Inject
    public ReadNextPlaceImpl(ServiceBus serviceBus, OutputHandlerMap outputmap) {
        super();
        this.outputmap = outputmap;
        this.serviceBus = serviceBus;
    }

    @Override
    public void start(JavaScriptObject prm, StateTransition<DesktopPlace> onDone, EventBus bus) {
        JsTransactionApplicationContext parameter = prm.cast();
        String outputHandler = applicationItem.getOutputHandler();
        if (outputHandler == null) {
            outputHandler = outputmap.getDefault();
        }

        if (parameter.getUserOutput() == null) {
            GWT.log("null user output");
        } else {
            JavaScriptObject propertieso = applicationItem.getPropertiesObject();
            serviceBus.parseOutput(outputHandler, propertieso, bus, context, parameter, onDone);
        }
    }

    @Override
    public void setContext(ProcessContextServices context) {
        this.context = context;
    }

    @Override
    public void setApplicationItem(JsApplicationItem applicationItem) {
        this.applicationItem = applicationItem;
    }

}
