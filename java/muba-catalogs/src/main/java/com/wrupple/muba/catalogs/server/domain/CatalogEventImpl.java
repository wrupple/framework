package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogEvent;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import org.apache.commons.chain.Context;

import java.util.List;

public class CatalogEventImpl extends CatalogEntryImpl implements CatalogEvent {
	private static final long serialVersionUID = -5221787594135912800L;
	private final String catalog;
	private final CatalogEntry entry;
	private List<Long> explicitlySuscriptedPeers;
	private List<CatalogEntry> oldValues;
	private CatalogActionContext stateValue;
	private Object state;


	public CatalogEventImpl(Long domain, String catalogId, String action, CatalogEntry entry){
		super();
		setDomain(domain);
		this.catalog = catalogId;
		setName(action);
		this.entry = entry;
		if(catalogId==null||action==null||entry==null){
			throw new NullPointerException(toString());
		}
	}
	public String getCatalog() {
		return catalog;
	}
	public CatalogEntry  getEntryValue(){
		return entry;
	}
	@Override
	public String toString() {
		return "CacheInvalidationEvent [catalogId=" + catalog + ", action=" + getName() + ", entry=" + entry + "]";
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public List<Long> getExplicitlySuscriptedPeers() {
		return explicitlySuscriptedPeers;
	}

	@Override
	public void setOldValues(List<CatalogEntry> oldValues) {
		this.oldValues=oldValues;
	}

	public List<CatalogEntry> getOldValues() {
		return oldValues;
	}

	@Override
	public CatalogActionContext getStateValue() {
		return stateValue;
	}

	@Override
	public void setStateValue(Context context) {
		this.stateValue = (CatalogActionContext) context;
	}

	public Object getEntryAsSerializable() {
		if(entry instanceof HasAccesablePropertyValues){
			return ((HasAccesablePropertyValues) entry).getAsSerializable();
		}else{
			return entry;
		}
	}
	@Override
	public void setCatalog(String catalog) {
		throw new IllegalStateException();
	}
	@Override
	public Object getEntry() {
		return entry.getId();
	}
	@Override
	public void setEntry(Object id) {
		throw new IllegalStateException();		
	}
	@Override
	public String getCatalogType() {
		return CatalogEvent.CATALOG;
	}

	@Override
	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}
}
