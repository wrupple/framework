package com.wrupple.muba.catalogs.client.services.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.shared.services.event.EntryCreatedEvent;

public class EntryCreationServiceHook extends DataCallback<JsCatalogEntry> {
	EventBus eventBus;
	String domain,host;
	public EntryCreationServiceHook(EventBus eventBus,String host,String domain) {
		super();
		this.host=host;
		this.domain= domain;
		this.eventBus = eventBus;
	}

	@Override
	public void execute() {
		if (result != null) {
			JsCatalogKey entry = result.cast();
			EntryCreatedEvent event = new EntryCreatedEvent(entry, host, domain);
			eventBus.fireEvent(event);
		}
	}

}
