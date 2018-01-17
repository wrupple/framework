package com.wrupple.muba.catalogs.client.services.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;

import java.util.Collections;

public class EntryRetrivingServiceHook extends DataCallback<JsCatalogEntry> {

	String catalogid;
	private EventBus eventBus;

	public static class List extends DataCallback<java.util.List<JsCatalogEntry>> {

		String catalogid;
		private EventBus eventBus;

		public List(String catalogid, EventBus eventBus) {
			super();
			assert catalogid!=null;
			assert eventBus!=null;
			this.catalogid = catalogid;
			this.eventBus = eventBus;
		}

		@Override
		public void execute() {
			if (result != null) {
				EntriesRetrivedEvent event = new EntriesRetrivedEvent(catalogid, result);
				eventBus.fireEvent(event);
			}
		}

	}

	public EntryRetrivingServiceHook(String catalogid, EventBus eventBus) {
		super();
		assert catalogid!=null;
		assert eventBus!=null;
		this.catalogid = catalogid;
		this.eventBus = eventBus;
	}

	@Override
	public void execute() {
		if (result != null) {
			fireeventAndStuff(catalogid, Collections.singletonList(result));
		}

	}

	private void fireeventAndStuff(String catalog, java.util.List<JsCatalogEntry> entries) {
		EntriesRetrivedEvent event = new EntriesRetrivedEvent(catalog, entries);
		eventBus.fireEvent(event);
	}

}
