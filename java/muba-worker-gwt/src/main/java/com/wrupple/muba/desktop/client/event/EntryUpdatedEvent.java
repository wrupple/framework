package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public class EntryUpdatedEvent extends GwtEvent<EntryUpdatedEventHandler> {
	private static Type<EntryUpdatedEventHandler> type;
	public final JsCatalogKey entry;
	public final String host,domain;

	public EntryUpdatedEvent(JsCatalogKey entry, String host, String domain) {
		this.entry = entry;
		this.domain=domain;
		this.host=host;
	}

	@Override
	protected void dispatch(EntryUpdatedEventHandler handler) {
		handler.onEntryUpdated(this);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EntryUpdatedEventHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<EntryUpdatedEventHandler> getType() {
		if(type==null){
			type=new Type<EntryUpdatedEventHandler>();
		}
		return type;
	}

}
