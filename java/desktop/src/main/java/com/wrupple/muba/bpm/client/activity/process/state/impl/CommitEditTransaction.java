package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractCommitUserTransactionImpl;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.event.BeforeEntryCreatedEvent;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogEntry;

public class CommitEditTransaction extends AbstractCommitUserTransactionImpl {

	
	@Override
	public void start(JsTransactionApplicationContext context,
                      StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		super.start(context, onDone, bus);
		//MOVED TO CommitSubmissionImpl
	}
}
