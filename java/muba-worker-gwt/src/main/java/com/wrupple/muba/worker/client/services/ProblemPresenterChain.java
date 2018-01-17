package com.wrupple.muba.worker.client.services;

import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;

public interface ProblemPresenterChain {

	void assembleTaskProcessSection(Process<?, ?> regreso,
			JsProcessTaskDescriptor step);
	

}
