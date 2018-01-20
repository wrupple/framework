package com.wrupple.muba.worker.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.state.CommitUserTransaction;
import com.wrupple.muba.worker.server.service.StateTransition;

public class CommitEditTransaction implements CommitUserTransaction {


    @Override
    public void start(JsTransactionApplicationContext context,
                      StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
        super.start(context, onDone, bus);
        //MOVED TO CommitSubmissionImpl
    }
}
