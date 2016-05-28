package com.wrupple.base.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.domain.FilterDataImpl;

public class CatalogResultCacheImpl implements CatalogResultCache {
	private static final Logger log = LoggerFactory.getLogger(CatalogResultCacheImpl.class);
	private long domain;
	private String catalog;

	private final CacheManager cachem;
	
	private Cache<Object, Object> entryCache;
	private Cache<Object, Object> listCache;

	@Inject
	public CatalogResultCacheImpl(CacheManager cachem) {
		this.cachem = cachem;
	}
	
	protected Cache<Object, Object> assertEntrycache(){
		if(entryCache==null){
			entryCache =cachem.getCache(domain+"_"+catalog);
		}
		return entryCache;
	}
	
	protected Cache<Object, Object> assertListcache(){
		if(listCache==null){
			listCache =cachem.getCache(domain+"_"+catalog+"_Lists");
		}
		return listCache;
	}

	public long getDomain() {
		return domain;
	}

	public void setDomain(long domain) {
		this.domain = domain;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public <T extends CatalogEntry> void shiftRemove(T o, FilterData filter) {
		Cache<Object, Object> cache = assertListcache();
		List<Object> ids = (List<Object>)cache.get(filter);
		ids.remove(o.getId());
		assertEntrycache().remove(o.getId());
	}

	@Override
	public <T extends CatalogEntry> void append(T o, FilterData filter) {
		Cache<Object, Object> cache = assertListcache();
		List<Object> ids = (List<Object>)cache.get(filter);
		ids.add(o.getId());
		put(o);
	}

	@Override
	public <T extends CatalogEntry> List<T> satisfy(FilterData filterData) {
		FilterDataImpl filter = (FilterDataImpl) filterData;
		Cache<Object, Object> cache = assertListcache();
		//TODO is a larger range satisfied that could be cut to satisfy this one?
		List<String> ids = (List<String>)cache.get(filter);
		List<T> results;
		if(ids==null){
			results=null;
		}else{
			results = new ArrayList<T> (ids.size());
			T result;
			for(String id: ids){
				result = get(id);
				if(result==null){
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
	public <T extends CatalogEntry> T get(Object targetEntryId) {
		T r = (T) assertEntrycache().get(targetEntryId);
		if(r!=null){
			log.trace("[CACHE SATISFIED ENTRY {}]",targetEntryId);
		}
		return r;
	}

	@Override
	public <T extends CatalogEntry> void put(T entry) {
		if(entry!=null){
		assertEntrycache().put(entry.getId(), entry);
		log.trace("[NEW CACHE ENTRY {}]",entry.getId());
		}
	}
	
	@Override
	public <T extends CatalogEntry> void update(T old, T newEntry) {
		put(newEntry);
	}
	

	@Override
	public <T extends CatalogEntry> void create(T entry) {
		put(entry);
		assertListcache().clear();
	}

	@Override
	public <T extends CatalogEntry> void delete(T entry) {
		if(entry!=null){
			assertEntrycache().remove(entry.getId());
			assertListcache().clear();
		}
	}

	@Override
	public <T extends CatalogEntry> void put(List<T> entrys, FilterData filterData) {
		
		List<Object> ids = new ArrayList<Object>(entrys.size());
		for(T entry: entrys){
			ids.add(entry.getId());
			put(entry);
		}
		assertListcache().put(filterData, ids );
	}

	@Override
	public void init(CatalogExcecutionContext context,String catalogId) {
		setDomain(context.getDomain().longValue());
		setCatalog(catalogId);
	}



}
