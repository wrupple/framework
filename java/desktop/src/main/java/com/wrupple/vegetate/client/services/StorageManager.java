package com.wrupple.vegetate.client.services;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.domain.Transaction;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogIdentification;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface StorageManager {

	public interface Unit<V> {

		Transaction startTransaction();
		
		void create(String domainNamespace, V entry,CatalogDescriptor catalog, final StateTransition<V> callback);

		<T extends JavaScriptObject> void read(String domainNamespace,String id, CatalogDescriptor catalog, final StateTransition<T> callback);

		<T extends JavaScriptObject> void read(String domainNamespace,JsFilterData filter, CatalogDescriptor catalog, StateTransition<List<T>> callback);

		void read(String domainNamespace, List<String> ids,CatalogDescriptor catalog, StateTransition<List<V>> retailersCallback);

		void update(String domainNamespace, String id,V entry, CatalogDescriptor catalog, final StateTransition<V> callback);

		void delete(String domainNamespace, String id,CatalogDescriptor catalog, final StateTransition<V> callback);
		
	}

	
	 RemoteStorageUnit<? super CatalogActionRequest,? super CatalogEntry> getRemoteStorageUnit(String host);

	void create(String host,String domainNamespace, String catalog, JsCatalogEntry entry, final StateTransition<JsCatalogEntry> callback);

	<T extends JavaScriptObject> void read(String host,String domainNamespace, String catalog, String id, final StateTransition<T> callback);

	<T extends JavaScriptObject> void read(String host,String domainNamespace, String catalog, JsFilterData filter, StateTransition<List<T>> callback);

	void read(String host,String domainNamespace, String catalog, List<String> ids, StateTransition<List<JsCatalogEntry>> retailersCallback);

	void update(String host,String domainNamespace, String catalog, String id, JsCatalogEntry entry, final StateTransition<JsCatalogEntry> callback);

	void delete(String host,String domainNamespace, String catalog, String id, final StateTransition<JsCatalogEntry> callback);


	/*
	 * CATALOG DESCRIPTION
	 */
	void loadCatalogDescriptor(String host, String domain, final String catalogId, final StateTransition<CatalogDescriptor> onDone);
	
	void loadGraphDescription(String host, String domain, String catalog, StateTransition<CatalogDescriptor> onDone);

	void loadCatalogNames(String host, String domain, StateTransition<List<JsCatalogIdentification>> callback);

	void putInCache(String host, String domain, CatalogDescriptor djso);
}