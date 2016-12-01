package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;

public abstract class DesktopProcessEvent extends GwtEvent<DesktopProcessEventHandler> {

	public static final Type<DesktopProcessEventHandler> TYPE = new Type<DesktopProcessEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DesktopProcessEventHandler> getAssociatedType() {
		return TYPE;
	}


}
