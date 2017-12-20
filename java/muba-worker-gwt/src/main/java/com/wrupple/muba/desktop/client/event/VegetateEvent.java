package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.GwtEvent;

public abstract class VegetateEvent extends GwtEvent<VegetateEventHandler> {

	public static final Type<VegetateEventHandler> TYPE=new Type<VegetateEventHandler>();
	
	private final int requestNumber;
	private final String channelId;
	private final String host;
	
	public VegetateEvent(int requestNumber,String channelId,String host) {
		super();
		this.requestNumber = requestNumber;
		this.channelId=channelId;
		this.host=host;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<VegetateEventHandler> getAssociatedType() {
		return TYPE;
	}

	public int getRequestNumber() {
		return requestNumber;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getHost() {
		return host;
	}

}
