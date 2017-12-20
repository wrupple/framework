package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CacheInvalidationEventListener extends EventHandler {

	void onCacheInvalidationEvent(CacheInvalidationEvent e);
	
	void onInvalidationChannelClosed(InvalidationChannelClosedEvent e);
	
}
