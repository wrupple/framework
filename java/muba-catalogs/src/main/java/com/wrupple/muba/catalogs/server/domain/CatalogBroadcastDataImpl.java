package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.catalogs.server.chain.command.PublishEvents.CatalogBroadcastData;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;

public class CatalogBroadcastDataImpl implements CatalogBroadcastData {

	private final String catalogId,action;
	private final CatalogEntry entry;
	private final Long domain;
	public CatalogBroadcastDataImpl(Long domain,String catalogId, String action, CatalogEntry entry){
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
		return "CatalogBroadcastDataImpl [catalogId=" + catalogId + ", action=" + action + ", entry=" + entry + "]";
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
