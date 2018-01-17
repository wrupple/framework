package com.wrupple.muba.worker.client.activity.process.state;

import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface MachineTask extends
        State.ContextAware<JsTransactionApplicationContext, JsTransactionApplicationContext> {

}
