package com.wrupple.muba.bpm.client.activity.process.state;

import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface ContextualTransactionProcessState extends
		State.ContextAware<JsTransactionActivityContext, JsTransactionActivityContext> {
	void setTaskDescriptor(JsProcessTaskDescriptor activityDescriptor);

}
