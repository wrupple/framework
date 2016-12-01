package com.wrupple.muba.catalogs.client.services.impl;

import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.event.EntriesDeletedEvent;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class EntryDeleteServiceHook extends DataCallback<JsCatalogEntry> {

	private String catalog,domain,host;
	private List<String> values;
	private EventBus eventBus;

	public EntryDeleteServiceHook(String catalog, List<String> values, EventBus eventBus,String host,String domain) {
		super();
		this.domain=domain;
		this.host=host;
		this.catalog = catalog;
		this.values = values;
		this.eventBus = eventBus;
	}

	@Override
	public void execute() {
		if (result != null ) {
			EntriesDeletedEvent event = new EntriesDeletedEvent(values, catalog, domain, host);
			eventBus.fireEvent(event);
		}
	}

}
