package com.wrupple.muba.bpm.client.activity.process.state;

import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface CommitUserTransaction extends
        State.ContextAware<JsTransactionApplicationContext, JsTransactionApplicationContext> {
	void setSaveTo(String task.getProducedField());

}
