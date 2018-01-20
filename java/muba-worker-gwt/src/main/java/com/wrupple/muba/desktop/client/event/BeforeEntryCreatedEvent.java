package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.server.service.StateTransition;

public class BeforeEntryCreatedEvent extends GwtEvent<BeforeEntryCreatedEventHandler> {
	static com.google.gwt.event.shared.GwtEvent.Type<BeforeEntryCreatedEventHandler> type;
	
	final JsCatalogEntry entry;
	
	StateTransition<Void> callback;
	
	boolean closed=true;

	private  final ProcessContextServices context;
	
	public BeforeEntryCreatedEvent(JsCatalogEntry entry, ProcessContextServices context) {
		this.entry=entry;
		this.context=context;
	}
	
	public ProcessContextServices getContext() {
		return context;
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BeforeEntryCreatedEventHandler> getAssociatedType() {
		return getType();
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public boolean isClosed() {
		return closed;
	}

	@Override
	protected void dispatch(BeforeEntryCreatedEventHandler handler) {
		handler.onBeforeEntryCreated(this);
	}
	public JsCatalogEntry getEntry() {
		return entry;
	}
	public StateTransition<Void> getCallback() {
		return callback;
	}
	
	public void setCallback(StateTransition<Void> callback) {
		this.callback = callback;
	}
	public static com.google.gwt.event.shared.GwtEvent.Type<BeforeEntryCreatedEventHandler> getType() {
		if(type==null){
			type = new com.google.gwt.event.shared.GwtEvent.Type<BeforeEntryCreatedEventHandler>();
		}
		return type;
	}

}
