package com.wrupple.muba.bpm.client.activity.process.state;

import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface UserInteractionState extends
		ContextualTransactionProcessState ,HumanTask<JsTransactionActivityContext, JsTransactionActivityContext>{

	public void setLayoutUnit(String s);
	
	public void setTransactionViewClass(String s);

	void setSaveTo(String task.getProducedField());

}
