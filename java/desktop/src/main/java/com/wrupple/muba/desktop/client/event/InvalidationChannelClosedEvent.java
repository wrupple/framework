package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class InvalidationChannelClosedEvent extends GwtEvent<CacheInvalidationEventListener> {

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CacheInvalidationEventListener> getAssociatedType() {
		return CacheInvalidationEvent.TYPE;
	}

	@Override
	protected void dispatch(CacheInvalidationEventListener handler) {
		
		handler.onInvalidationChannelClosed(this);
	}

}
