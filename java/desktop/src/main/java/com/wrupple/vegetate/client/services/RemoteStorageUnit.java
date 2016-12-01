package com.wrupple.vegetate.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.vegetate.client.services.StorageManager.Unit;
import com.wrupple.vegetate.domain.CatalogEntry;

/**
 * Catalog communication service with a remote server using vegetate as a REST
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

	public SafeUri getImageUri(String domain, String imageId, int customSize);

	void assertManifest(DataCallback<Void> dataCallback);
	
	void setHost(BPMPeer peer);
	
}
