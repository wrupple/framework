package com.wrupple.muba.desktop.client.event;

import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;

public class NewVegetateRequestEvent extends VegetateEvent {
	private final Object cargo;
	private final JsVegetateServiceManifest  addressTokens;
	private final String requestURL;
	
	public NewVegetateRequestEvent(String host,int requestNumber, String channelId,
			Object cargo, JsVegetateServiceManifest  addressTokens, String requestURL) {
		super(requestNumber, channelId,host);
		this.cargo = cargo;
		this.addressTokens = addressTokens;
		this.requestURL = requestURL;
	}



	@Override
	protected void dispatch(VegetateEventHandler handler) {
		handler.onNewVegetateRequest(this);
	}


	public Object getCargo() {
		return cargo;
	}



	public JsVegetateServiceManifest  getAddressTokens() {
		return addressTokens;
	}



	public String getRequestURL() {
		return requestURL;
	}

}
