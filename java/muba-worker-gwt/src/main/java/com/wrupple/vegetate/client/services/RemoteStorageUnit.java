package com.wrupple.vegetate.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.shared.services.StorageManager.Unit;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.domain.BPMPeer;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.CatalogEntry;

/**
 * Catalog communication service setRuntimeContext a remote server using vegetate as a REST
 * communication standard
 * 
 * provides additional catalog services such as image builders
 * 
 * @author japi
 *
 */
public interface RemoteStorageUnit<R extends CatalogActionRequest,V  extends CatalogEntry> extends Unit<V> {
	/*
	 * Formatted Services
	 */

	String UNIT = "remote";

	void callStringArrayService(R action, StateTransition callback);

	<T extends JavaScriptObject> void callGenericService(R action, StateTransition<T> callback);

	/*
	 * Url Builders
	 */

	String buildServiceUrl(R request);

    SafeUri getImageUri(String domain, String imageId, int customSize);

	void assertManifest(DataCallback<Void> dataCallback);
	
	void setHost(BPMPeer peer);
	
}
