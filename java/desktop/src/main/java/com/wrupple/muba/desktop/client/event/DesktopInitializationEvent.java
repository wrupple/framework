package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;

public abstract class DesktopInitializationEvent extends GwtEvent<HandlesDesktopInitializationEvents> {

	public static final Type<HandlesDesktopInitializationEvents> TYPE = new Type<HandlesDesktopInitializationEvents>();

	@Override
	protected abstract void dispatch(HandlesDesktopInitializationEvents handler);

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<HandlesDesktopInitializationEvents> getAssociatedType() {
		return TYPE;
	}

}
