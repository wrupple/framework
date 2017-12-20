package com.wrupple.muba.bpm.client.services;

import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;

public interface TransactionAssembler {

	void assembleTaskProcessSection(Process<?, ?> regreso,
			JsProcessTaskDescriptor step);
	

}
