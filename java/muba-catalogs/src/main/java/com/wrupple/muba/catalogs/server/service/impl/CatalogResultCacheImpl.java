package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.impl.FilterCriteriaImpl;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.inject.Inject;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.impl.FilterDataImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;

@Singleton
public class CatalogResultCacheImpl implements CatalogResultCache {

	private static Logger log = LogManager.getLogger(CatalogResultCacheImpl.class);

	private final Context cachem;

	@Inject
	public CatalogResultCacheImpl(@Named("catalog.cache") Context cachem) {
		this.cachem = cachem;
	}

	@Override
	public <T extends CatalogEntry> T get(CatalogActionContext context, String catalogId, Object targetEntryId) {
		long domain = ((Long)context.getNamespaceContext().getId()).longValue();
		T r = (T) assertEntrycache(domain, catalogId).get(targetEntryId);
		if (r != null) {
			log.trace("[CACHE SATISFIED ENTRY {}]", targetEntryId);
		}
		return r;
	}

    @Override
    public <T extends CatalogEntry> T get(CatalogActionContext context, String catalogId, String explicitField, Object targetEntryId) {
        long domain = ((Long)context.getNamespaceContext().getId()).longValue();
		Object key = assertFieldCache(domain, catalogId, explicitField).get(targetEntryId);
		if(key==null){
			return null;
		}
        T r = (T) get(context,catalogId,key);
        if (r != null) {
            log.trace("[CACHE SATISFIED ENTRY {}]", targetEntryId);
        }
        return r;
    }


    @Override
    public <T extends CatalogEntry> List<T> satisfy(CatalogActionContext context, CatalogDescriptor catalog,
                                                    FilterData filterData) {
        String catalogId = catalog.getDistinguishedName();
        FilterDataImpl filter = (FilterDataImpl /* hashcode implementation */) filterData;
        long domain = ((Long)context.getNamespaceContext().getId()).longValue();
        Map<Object, Object> cache = assertListcache(domain, catalogId);

        List<Object> ids = assertList(cache, filter);
        List<T> results;
        if (ids.isEmpty()) {
            results = null;

            if(filter.getFilters().size()==1){
                FilterCriteriaImpl keyCriteria = filter.fetchCriteria(catalog.getKeyField());
                if(keyCriteria!=null){
                    ids = keyCriteria.getValues();
                    results=satisfyIds(ids,cache,context,catalogId);
                }
            }

        } else {
            results=satisfyIds(ids,cache,context,catalogId);
            if (results == null) {
                cache.remove(filter);
            }
        }
        if(results!=null){
            log.trace("[CACHE SATISFIED QUERY]");
        }
        return results;
    }


    @Override
	public void put(CatalogActionContext context, String catalogId, CatalogEntry entry) {
	    if(entry.getId()==null){
	        throw new IllegalArgumentException("unidentified entries may not be cached ("+entry+")");
        }
        long domain = ((Long)context.getNamespaceContext().getId()).longValue();
        assertEntrycache(domain, catalogId).put(entry.getId(), entry);
        log.trace("[NEW CACHE ENTRY {}]", entry.getId());
	}

	@Override
	public void put(CatalogActionContext context, String catalogId, Object explicitKey, String explicitField, CatalogEntry regreso) {
		long domain = ((Long)context.getNamespaceContext().getId()).longValue();
		assertFieldCache(domain, catalogId,explicitField).put(explicitKey, regreso.getId());
		log.trace("[NEW CACHE ENTRY {}]", explicitKey);
	}

	@Override
	public void put(CatalogActionContext context, String catalogId, List<CatalogEntry> entrys, FilterData filterData) {

		List<Object> ids = new ArrayList<Object>(entrys.size());
		for (CatalogEntry entry : entrys) {
			ids.add(entry.getId());
			put(context, catalogId, entry);
		}
		long domain = ((Long)context.getRequest().getDomain()).longValue();
		assertListcache(domain, catalogId).put(filterData, ids);
	}

	private <T extends CatalogEntry> List<T> satisfyIds(List<Object> ids, Map<Object, Object> cache, CatalogActionContext context, String catalogId) {
		List<T> results = new ArrayList<T>(ids.size());
		T result;
		for (Object id : ids) {
			result = get(context, catalogId, id);
			if (result == null) {
				return null;
			}
			results.add(result);
		}
		return results;
	}

	public void update(CatalogActionContext context, String catalog, CatalogEntry oldValue, CatalogEntry result) {
		put(context, catalog, result);
	}

	@Override
	public void delete(CatalogActionContext context, String catalogId, Object targetEntryId) {
		if (targetEntryId != null) {
			long domain = ((Long)context.getRequest().getDomain()).longValue();
			assertEntrycache(domain, catalogId).remove(targetEntryId);
			assertListcache(domain, catalogId).clear();
		}
	}

	@Override
	public void clearLists(CatalogActionContext context, String catalogId) {
		long domain = ((Long)context.getRequest().getDomain()).longValue();
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
		listCache = (Map<Object, Object>) cachem.get(domain + "_" + catalog + "__Lists");
		if (listCache == null) {
			listCache = new HashMap<Object, Object>();
			cachem.put(domain + "_" + catalog + "__Lists", listCache);
		}
		return listCache;
	}

	private Map<Object, Object> assertFieldCache(long domain, String catalog,String field) {
		Map<Object, Object> fieldCache;
		fieldCache = (Map<Object, Object>) cachem.get(domain + "_" + catalog + "_"+field);
		if (fieldCache == null) {
			fieldCache = new HashMap<Object, Object>();
			cachem.put(domain + "_" + catalog + "_"+field, fieldCache);
		}
		return fieldCache;
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
