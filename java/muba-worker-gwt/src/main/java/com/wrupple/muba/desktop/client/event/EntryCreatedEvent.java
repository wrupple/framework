package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public class EntryCreatedEvent extends GwtEvent<EntryCreatedEventHandler> {
	private static Type<EntryCreatedEventHandler> type;
	public final JsCatalogKey entry;
	public final String host,domain;

	public EntryCreatedEvent(JsCatalogKey result, String host, String domain) {
		this.entry = result;
		this.domain=domain;
		this.host=host;
	}

	@Override
	protected void dispatch(EntryCreatedEventHandler handler) {
		handler.onEntryCreated(this);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EntryCreatedEventHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<EntryCreatedEventHandler> getType() {
		if(type==null){
			type=new Type<EntryCreatedEventHandler>();
		}
		return type;
	}

}
