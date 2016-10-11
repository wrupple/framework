package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CacheInvalidationEvent;

public class CacheInvalidationEventImpl implements CacheInvalidationEvent {

	private final String catalogId,action;
	private final CatalogEntry entry;
	private final Long domain;
	public CacheInvalidationEventImpl(Long domain,String catalogId, String action, CatalogEntry entry){
		super();
		this.domain=domain;
		this.catalogId = catalogId;
		this.action = action;
		this.entry = entry;
		if(catalogId==null||action==null||entry==null){
			throw new NullPointerException(toString());
		}
	}
	public String getCatalogId() {
		return catalogId;
	}
	public String getAction() {
		return action;
	}
	public CatalogEntry  getEntry(){
		return entry;
	}
	@Override
	public String toString() {
		return "CacheInvalidationEvent [catalogId=" + catalogId + ", action=" + action + ", entry=" + entry + "]";
	}
	@Override
	public Object getEntryAsSerializable() {
		if(entry instanceof HasAccesablePropertyValues){
			return ((HasAccesablePropertyValues) entry).getAsSerializable();
		}else{
			return entry;
		}
	}
	public Long getDomain() {
		return domain;
	}
	

}
