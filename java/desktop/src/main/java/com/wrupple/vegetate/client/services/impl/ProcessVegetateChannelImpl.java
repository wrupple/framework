package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.UrlBuilder;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.server.service.ProcessServiceManifest;
import com.wrupple.muba.desktop.client.services.logic.SerializationService;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTaskProcessRequest;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;
import com.wrupple.muba.desktop.domain.overlay.JsonVegetateResponse;
import com.wrupple.vegetate.client.services.ProcessVegetateChannel;
import com.wrupple.vegetate.domain.VegetateServiceManifest;

public class ProcessVegetateChannelImpl extends SimpleVegetateChannel<JsTaskProcessRequest, JsCatalogEntry> implements ProcessVegetateChannel{

	
	//FIXME hook BPMengine to a jexl api so triggers can invoke Artificial inteligence stuff
	@Inject
	public ProcessVegetateChannelImpl(String hostOrNull,boolean ssl,VegetateServiceManifest manifest, EventBus bus, SerializationService<JsTaskProcessRequest, JsonVegetateResponse> serializer, String publicKey, String privateKey) {
		super(manifest, bus, ssl?"https":"http", RequestBuilder.POST, ProcessServiceManifest.CHANNEL_ID, serializer, null, hostOrNull, UrlBuilder.PORT_UNSPECIFIED, publicKey, privateKey);
		GWT.log("[new] BPM Channel");
	}
	public static JsVegetateServiceManifest manifest;
	public static ProcessVegetateChannelImpl temp;
	public static StateTransition<ProcessVegetateChannelImpl> callback;
	
}

