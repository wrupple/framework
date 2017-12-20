package com.wrupple.muba.bpm.client.activity.process.state;

import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface InitializeActivityContext extends
		State.ContextAware<DesktopPlace, JsTransactionApplicationContext> {
	
	void setApplicationItem(JsApplicationItem item);

}
