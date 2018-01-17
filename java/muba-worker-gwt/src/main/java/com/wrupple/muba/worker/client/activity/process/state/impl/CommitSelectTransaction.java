package com.wrupple.muba.worker.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.state.AbstractCommitUserTransactionImpl;

public class CommitSelectTransaction extends AbstractCommitUserTransactionImpl {


    @Override
    public void start(JsTransactionApplicationContext context,
                      StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
        MOVED TO CommitSubmissionImpl
    }
}
