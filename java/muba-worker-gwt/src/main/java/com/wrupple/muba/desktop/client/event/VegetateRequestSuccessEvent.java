package com.wrupple.muba.desktop.client.event;

public class VegetateRequestSuccessEvent extends VegetateEvent {

	private final Object response;
	
	public VegetateRequestSuccessEvent(String host,int requestNumber, String channelId,
			Object response) {
		super(requestNumber, channelId,host);
		this.response = response;
	}

	@Override
	protected void dispatch(VegetateEventHandler handler) {
		handler.onRequestSuccessful(this);
	}

	public Object getResponse() {
		return response;
	}

}
