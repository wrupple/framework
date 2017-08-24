package com.wrupple.muba.catalogs.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.wrupple.muba.event.domain.CatalogEntry;

public class PersistentCatalogEntityImpl implements PersistentCatalogEntity {
	private static final long serialVersionUID = -8295920370428632912L;
	private CatalogDescriptor type;
	private Map<String, Object> persistentProperties;

	@Inject
	public PersistentCatalogEntityImpl() {
		persistentProperties = new HashMap<String, Object>();
	}
	
	public PersistentCatalogEntityImpl(CatalogDescriptor type,Map<String, Object> persistentProperties) {
		this();
		setPersistentProperties(persistentProperties);
		this.type = type;
	}

	public void setPersistentProperties(Map<String, Object> persistentProperties) {
		this.persistentProperties = persistentProperties;
	}

	public Map<String, Object> getPersistentProperties() {
		return persistentProperties;
	}

	public Object getId() {
		return persistentProperties.get(CatalogEntry.ID_FIELD);
	}


	@Override
	public String getName() {
		return (String) persistentProperties.get(CatalogEntry.NAME_FIELD);
	}

	@Override
	public void setName(String name) {
		persistentProperties.put(CatalogEntry.NAME_FIELD, name);
	}

	/*
	 * @Override public Date getTimestamp() { Date l = (Date)
	 * persistentProperties.getProperty("timestamp"); if(l==null){ return null;
	 * }else{ return l; }
	 * 
	 * }
	 * 
	 * @Override public void setTimestamp(Date d) {
	 * persistentProperties.setProperty("timestamp", d); }
	 */

	@Override
	public Long getDomain() {
		return CatalogEntry.WRUPPLE_ID;
	}

	@Override
	public void setDomain(Long domain) {
	}

	@Override
	public boolean isAnonymouslyVisible() {
		Boolean visible = (Boolean) persistentProperties.get("anonymouslyVisible");
		if (visible == null) {
			return false;
		} else {
			return visible;
		}
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {
		persistentProperties.put("anonymouslyVisible", p);
	}

	@Override
	public Object getPropertyValue(String fieldId) {
		return persistentProperties.get(fieldId);
	}

	@Override
	public Object setPropertyValue(Object value, String fieldId) {
		persistentProperties.put(fieldId, value);
		return value;
	}

	@Override
	public String toString() {
		return "[persistentProperties=" + persistentProperties + "]";
	}

	@Override
	public String getCatalogType() {
		return null;
	}

	@Override
	public String getImage() {
		return (String) persistentProperties.get(PersistentImageMetadata.IMAGE_FIELD);
	}

	@Override
	public CatalogDescriptor getType() {
		return type;
	}

	@Override
	public Object getAsSerializable() {
		return persistentProperties;
	}

	@Override
	public List<String> getProperties() {
		return Collections.unmodifiableList(new ArrayList(persistentProperties.keySet()));
	}

	@Override
	public void setProperties(List<String> properties) {

	}

	@Override
	public void initCatalog(CatalogDescriptor catalog) {
		this.type = catalog;
	}

}
