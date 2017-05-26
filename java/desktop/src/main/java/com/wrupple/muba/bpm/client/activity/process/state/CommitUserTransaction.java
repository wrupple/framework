package com.wrupple.muba.bpm.client.activity.process.state;

import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface CommitUserTransaction extends
        State.ContextAware<JsTransactionActivityContext, JsTransactionActivityContext> {
	void setSaveTo(String task.getProducedField());

}
