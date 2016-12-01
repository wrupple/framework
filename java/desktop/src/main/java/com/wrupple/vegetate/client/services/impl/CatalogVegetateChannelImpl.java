package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.UrlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.desktop.client.services.logic.SerializationService;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionResult;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;
import com.wrupple.muba.desktop.domain.overlay.JsonVegetateResponse;

public class CatalogVegetateChannelImpl extends
		SimpleVegetateChannel<JsCatalogActionRequest, JsCatalogActionResult> implements com.wrupple.vegetate.client.services.CatalogVegetateChannel {
	
	public CatalogVegetateChannelImpl(BPMPeer peer,boolean ssl,JsVegetateServiceManifest manifest, EventBus bus,
			SerializationService<JsCatalogActionRequest, JsonVegetateResponse> serializer) {
		super(manifest, bus, ssl?"https":"http", RequestBuilder.POST, peer.getUrlBase(), serializer, null, peer.getHost(), UrlBuilder.PORT_UNSPECIFIED,peer.getPublicKey(),peer.getPrivateKey());
		GWT.log("[new] CatalogChannel");
	}

}
