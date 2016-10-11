package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.domain.FilterDataImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;

@Singleton
public class CatalogResultCacheImpl implements CatalogResultCache {

	private static Logger log = LoggerFactory.getLogger(CatalogResultCacheImpl.class);

	private final Context cachem;

	@Inject
	public CatalogResultCacheImpl(@Named("catalog.cache") Context cachem) {
		this.cachem = cachem;
	}

	@Override
	public <T extends CatalogEntry> T get(CatalogActionContext context, String catalogId, Object targetEntryId) {
		long domain = context.getDomain().longValue();
		T r = (T) assertEntrycache(domain, catalogId).get(targetEntryId);
		if (r != null) {
			log.trace("[CACHE SATISFIED ENTRY {}]", targetEntryId);
		}
		return r;
	}

	@Override
	public void put(CatalogActionContext context, String catalogId, CatalogEntry entry) {
		if (entry != null) {
			put(context, catalogId, entry.getId(), entry);
		}
	}

	@Override
	public void put(CatalogActionContext context, String catalogId, Object explicitKey, CatalogEntry entry) {
		long domain = context.getDomain().longValue();
		assertEntrycache(domain, catalogId).put(explicitKey, entry);
		log.trace("[NEW CACHE ENTRY {}]", entry.getId());
	}

	@Override
	public void put(CatalogActionContext context, String catalogId, List<CatalogEntry> entrys, FilterData filterData) {

		List<Object> ids = new ArrayList<Object>(entrys.size());
		for (CatalogEntry entry : entrys) {
			ids.add(entry.getId());
			put(context, catalogId, entry);
		}
		long domain = context.getDomain().longValue();
		assertListcache(domain, catalogId).put(filterData, ids);
	}

	@Override
	public <T extends CatalogEntry> List<T> satisfy(CatalogActionContext context, String catalogId,
			FilterData filterData) {
		FilterDataImpl filter = (FilterDataImpl /* hashcode implementation */) filterData;
		long domain = context.getDomain().longValue();
		Map<Object, Object> cache = assertListcache(domain, catalogId);

		// TODO is a larger range satisfied that could be cut to satisfy this
		// one? i personally don't think it's a good idea to implement that, but
		// hey! maybe?
		List<Object> ids = assertList(cache, filter);
		List<T> results;
		if (ids.isEmpty()) {
			results = null;
		} else {
			results = new ArrayList<T>(ids.size());
			T result;
			for (Object id : ids) {
				result = get(context, catalogId, id);
				if (result == null) {
					cache.remove(filter);
					return null;
				}
				results.add(result);
			}
			log.trace("[CACHE SATISFIED QUERY]");
		}

		return results;
	}

	@Override
	public void update(CatalogActionContext context, String catalog, CatalogEntry oldValue, CatalogEntry result) {
		put(context, catalog, result);
	}

	@Override
	public void delete(CatalogActionContext context, String catalogId, Object targetEntryId) {
		if (targetEntryId != null) {
			long domain = context.getDomain().longValue();
			assertEntrycache(domain, catalogId).remove(targetEntryId);
			assertListcache(domain, catalogId).clear();
		}
	}

	@Override
	public void clearLists(CatalogActionContext context, String catalogId) {
		long domain = context.getDomain().longValue();
		assertListcache(domain, catalogId).clear();
	}

	private Map<Object, Object> assertEntrycache(long domain, String catalog) {
		Map<Object, Object> entryCache;
		entryCache = (Map<Object, Object>) cachem.get(domain + "_" + catalog);
		if (entryCache == null) {
			entryCache = new HashMap<Object, Object>();
			cachem.put(domain + "_" + catalog, entryCache);
		}
		return entryCache;
	}

	private Map<Object, Object> assertListcache(long domain, String catalog) {
		Map<Object, Object> listCache;
		listCache = (Map<Object, Object>) cachem.get(domain + "_" + catalog + "_Lists");
		if (listCache == null) {
			listCache = new HashMap<Object, Object>();
			cachem.put(domain + "_" + catalog + "_Lists", listCache);
		}
		return listCache;
	}

	private List<Object> assertList(Map<Object, Object> cache, FilterData filter) {
		List<Object> ids = (List<Object>) cache.get(filter);
		if (ids == null) {
			ids = new ArrayList<Object>();
			cache.put(filter, ids);
		}
		return ids;
	}

	


}
