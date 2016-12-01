package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.domain.overlay.JsCacheInvalidationData;

public class CacheInvalidationEvent extends GwtEvent<CacheInvalidationEventListener>{
	public static final com.google.gwt.event.shared.GwtEvent.Type<CacheInvalidationEventListener> TYPE = new com.google.gwt.event.shared.GwtEvent.Type<CacheInvalidationEventListener>();
	final public JsCacheInvalidationData data;
	
	@Inject
	public CacheInvalidationEvent(JsCacheInvalidationData data) {
		super();
		this.data = data;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CacheInvalidationEventListener> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CacheInvalidationEventListener handler) {
		handler.onCacheInvalidationEvent(this);
	}

}
