package com.wrupple.muba.bpm.server.chain.command;

import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import org.apache.commons.chain.Command;

public interface UserInteractionState extends Command,
		ContextualTransactionProcessState ,HumanTask<JsTransactionActivityContext, JsTransactionActivityContext>{

	public void setLayoutUnit(String s);
	
	public void setTransactionViewClass(String s);

	void setSaveTo(String task.getProducedField());

}
