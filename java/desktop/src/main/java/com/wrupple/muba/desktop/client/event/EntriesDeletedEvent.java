package com.wrupple.muba.desktop.client.event;

import java.util.Collection;

import com.google.gwt.event.shared.GwtEvent;

public class EntriesDeletedEvent extends GwtEvent<EntriesDeletedEventHandler> {

	private static Type<EntriesDeletedEventHandler> type;
	public final Collection<String> entries;
	public final String catalog,domain,host;

	public EntriesDeletedEvent(Collection<String> values, String catalog, String domain, String host) {
		this.catalog = catalog;
		this.entries = values;
		this.domain=domain;
		this.host=host;
	}

	@Override
	protected void dispatch(EntriesDeletedEventHandler handler) {
		handler.onEntriesDeleted(this);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EntriesDeletedEventHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<EntriesDeletedEventHandler> getType() {
		if(type==null){
			type=new Type<EntriesDeletedEventHandler>();
		}
		return type;
	}


}