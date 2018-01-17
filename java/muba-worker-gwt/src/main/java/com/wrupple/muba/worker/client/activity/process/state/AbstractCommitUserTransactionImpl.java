package com.wrupple.muba.worker.client.activity.process.state;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

public class AbstractCommitUserTransactionImpl implements CommitUserTransaction {

    protected ProcessContextServices context;
    protected JsProcessTaskDescriptor activityDescriptor;
    private String saveTo;

    @Override
    public void start(JsTransactionApplicationContext contextP, StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
    }

    protected static class EntryUpdateCallback extends DataCallback<JsCatalogEntry> {

        JsTransactionApplicationContext context;
        StateTransition<JsTransactionApplicationContext> onDone;

        public EntryUpdateCallback(JsTransactionApplicationContext context, StateTransition<JsTransactionApplicationContext> onDone) {
            super();
            this.context = context;
            this.onDone = onDone;
        }

        @Override
        public void execute() {
            context.setTargetEntryId(result.getId());
            if (context.getTargetEntryId() == null) {
                throw new IllegalArgumentException("Commited entry setRuntimeContext no Id");
            }
            context.setUserOutput(result);
            onDone.setResultAndFinish(context);
        }

    }


}
