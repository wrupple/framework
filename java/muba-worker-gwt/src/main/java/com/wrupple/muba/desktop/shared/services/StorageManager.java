package com.wrupple.muba.desktop.shared.services;


import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.List;

public interface StorageManager {

    interface Unit<V> {

		void create(String domainNamespace, V entry,CatalogDescriptor catalog, final StateTransition<V> callback);

        <T> void read(String domainNamespace, String id, CatalogDescriptor catalog, final StateTransition<T> callback);

        <T> void read(String domainNamespace, FilterData filter, CatalogDescriptor catalog, StateTransition<List<T>> callback);

		void read(String domainNamespace, List<String> ids,CatalogDescriptor catalog, StateTransition<List<V>> retailersCallback);

		void update(String domainNamespace, String id,V entry, CatalogDescriptor catalog, final StateTransition<V> callback);

		void delete(String domainNamespace, String id,CatalogDescriptor catalog, final StateTransition<V> callback);
		
	}

	
	 RemoteStorageUnit<? super CatalogActionRequest,? super CatalogEntry> getRemoteStorageUnit(String host);

	void create(String host,String domainNamespace, String catalog, CatalogEntry entry, final StateTransition<CatalogEntry> callback);

	<T extends JavaScriptObject> void read(String host,String domainNamespace, String catalog, String id, final StateTransition<T> callback);

	<T extends JavaScriptObject> void read(String host,String domainNamespace, String catalog, JsFilterData filter, StateTransition<List<T>> callback);

	void read(String host,String domainNamespace, String catalog, List<String> ids, StateTransition<List<CatalogEntry>> retailersCallback);

	void update(String host,String domainNamespace, String catalog, String id, CatalogEntry entry, final StateTransition<CatalogEntry> callback);

	void delete(String host,String domainNamespace, String catalog, String id, final StateTransition<CatalogEntry> callback);


	/*
	 * CATALOG_TIMELINE DESCRIPTION
	 */
	void loadCatalogDescriptor(String host, String domain, final String catalogId, final StateTransition<CatalogDescriptor> onDone);

	void loadGraphDescription(String host, String domain, String catalog, StateTransition<CatalogDescriptor> onDone);

	void loadCatalogNames(String host, String domain, StateTransition<List<CatalogEntry>> callback);

	void putInCache(String host, String domain, CatalogDescriptor djso);
}