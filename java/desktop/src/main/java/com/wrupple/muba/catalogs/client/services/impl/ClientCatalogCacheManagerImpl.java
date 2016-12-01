package com.wrupple.muba.catalogs.client.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsFilterDataOrdering;

public class ClientCatalogCacheManagerImpl implements ClientCatalogCacheManager {
	static final char FILTER_KEY_SEPARATOR = '_';
	static final String FILTER_KEY_NULL = "0";
	final private Provider<CatalogCache> provider;
	private final JavaScriptObject fullcaches;
	private List<CatalogCache> invalidationQueue;
	private Map<String, Map<String, CatalogCache>> queryCaches;

	@Inject
	public ClientCatalogCacheManagerImpl(Provider<CatalogCache> provider) {
		super();
		this.provider = provider;
		fullcaches = JavaScriptObject.createObject();

	}

	@Override
	public CatalogCache getCache(JsCatalogDescriptor catalog, JavaScriptObject properties) {
		String policy = catalog.getCachePolicy();
		String catalogid = catalog.getCatalogId();
		if (policy == null) {
			policy = CatalogActionRequest.FULL_CACHE;
		}

		// TODO use configuration framework
		if (CatalogActionRequest.FULL_CACHE.equals(policy)) {
			return getIdentityCache(catalogid);
		}
		if (CatalogActionRequest.QUERY_CACHE.equals(policy)) {
			return getQueryCache(catalogid, (JsFilterData) properties);
		} else {
			return null;
		}
	}

	private CatalogCache getQueryCache(String catalogid, JsFilterData properties) {

		if (queryCaches == null) {
			queryCaches = new HashMap<String, Map<String, CatalogCache>>();
		}

		Map<String, CatalogCache> queryMap = queryCaches.get(catalogid);
		if (queryMap == null) {
			queryMap = new HashMap<String, CatalogCache>(2);
			queryCaches.put(catalogid, queryMap);
		}

		String filterKey = generateFilterKey(properties);

		CatalogCache regreso =  queryMap.get(filterKey);

		if(regreso==null){
			regreso = new QueryCache(getIdentityCache(catalogid));
			queryMap.put(filterKey, regreso);
		}
		return regreso;
	}

	private String generateFilterKey(JsFilterData filter) {
		JsArray<JsFilterCriteria> filters = filter.getFilterArray();
		JsArray<JsFilterDataOrdering> order = filter.getOrderArray();

		StringBuilder builder = new StringBuilder(250);
		builder.append(FILTER_KEY_SEPARATOR);
		builder.append(((filters == null) ? FILTER_KEY_NULL : new JSONObject(filters).toString()));
		builder.append(FILTER_KEY_SEPARATOR);
		builder.append(((order == null) ? 0 : new JSONObject(order).toString()));
		return builder.toString();
	}

	private native void put(JavaScriptObject cache, String key, CatalogCache element) /*-{
																								cache[key] = element;
																								}-*/;

	private native CatalogCache get(String key, JavaScriptObject cache) /*-{
																				return cache[key];
																				}-*/;

	@Override
	public CatalogCache getIdentityCache(String catalogid) {
		CatalogCache regreso = get(catalogid, fullcaches);
		if (regreso == null) {
			regreso = provider.get();
			regreso.setCatalog(catalogid);
			put(fullcaches, catalogid, regreso);
		}
		return regreso;
	}

	@Override
	public void invalidateCache(String catalogid) {
		queryCaches.remove(catalogid);
		CatalogCache regreso = get(catalogid, fullcaches);
		if (regreso == null) {
			return;
		}
		if (invalidationQueue == null) {
			regreso.invalidate();
		} else {
			invalidationQueue.add(regreso);
		}
	}

	@Override
	public void preventInvalidation() {
		if (invalidationQueue == null) {
			invalidationQueue = new ArrayList<CatalogCache>();
		}
	}

	@Override
	public void resumeInvalidation() {
		for (CatalogCache e : invalidationQueue) {
			e.invalidate();
		}
		invalidationQueue = null;
	}

	@Override
	public boolean isInvalidationAvailable() {
		return invalidationQueue == null;
	}

	@Override
	public void forceInvalidation(String catalogid) {
		queryCaches.remove(catalogid);
		CatalogCache regreso = get(catalogid, fullcaches);
		if (regreso == null) {
			return;
		}
		regreso.invalidate();
	}

}
