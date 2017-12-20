package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

import java.util.List;

public class EntriesRetrivedEvent extends GwtEvent<EntriesRetrivedEventHandler> {

	private static Type<EntriesRetrivedEventHandler> type;
	private String catalog;
	private List<JsCatalogEntry> entries;

	public EntriesRetrivedEvent(String catalog2, List<JsCatalogEntry> entries2) {
		this.catalog = catalog2;
		this.entries = entries2;
	}


	public String getCatalog() {
		return catalog;
	}

	public List<JsCatalogEntry> getEntries() {
		return entries;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EntriesRetrivedEventHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<EntriesRetrivedEventHandler> getType() {
		if(type==null){
			type=new Type<EntriesRetrivedEventHandler>();
		}
		return type;
	}


	@Override
	protected void dispatch(EntriesRetrivedEventHandler handler) {
		handler.onEntriesRetrived(this);
	}

}
