package com.wrupple.vegetate.server.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterDataOrdering;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.VegetateUserException;
import com.wrupple.vegetate.server.chain.command.I18nProcessing;
import com.wrupple.vegetate.server.domain.annotations.CatalogField;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;
import com.wrupple.vegetate.server.domain.annotations.ConsistentFields;
import com.wrupple.vegetate.server.domain.annotations.InheritanceTree;

public class CatalogDescriptorImpl implements CatalogDescriptor {
	
	private static final long serialVersionUID = 7222404658673284250L;
	private Long id;
	
	private Long domain;
	
	@CatalogKey(foreignCatalog=VegetatePeer.CATALOG)
	private Long peer;
	
	@CatalogKey(foreignCatalog=CatalogDescriptor.CATALOG_ID)
	@InheritanceTree
	private Long ancestor;
	
	private String name, image;
	
	private String catalogId;
	
	@CatalogFieldValues(defaultValueOptions={"Map"})
	private String clazz;
	
	@CatalogFieldValues(defaultValueOptions={CLOUD_STORAGE_UNIT,LOCAL,LOCAL_KEY_VALUE_PAIR,LOCAL_CACHE,CLOUD_CACHE,SECURE})
	
	
	private int storage;
	
	@CatalogFieldValues(defaultValueOptions={I18nProcessing.CONSOLIDATED,I18nProcessing.DISTRIBUTED})
	private int localization;
	
	@CatalogFieldValues(defaultValueOptions={CatalogActionRequest.FULL_CACHE, CatalogActionRequest.QUERY_CACHE, CatalogActionRequest.NO_CACHE})
	private int optimization  /* cachePolicy */;
	
	private boolean consolidated, revised, versioned,
			anonymouslyVisible/*
								 * persisted with jdo strategy, no namespace,
								 * saving domain data as a field
								 */;
	
	@ConsistentFields
	@CatalogKey(foreignCatalog=FieldDescriptor.CATALOG_ID)
	private List<Long> formFields;
	
	@CatalogKey(foreignCatalog=CatalogActionTrigger.CATALOG)
	private List<Long> triggers;
	
	private List<String> contextExpressions, properties, sorts, criteria;

	
	private List<CatalogActionTrigger> triggersValues;
	
	@CatalogField(ignore = true)
	private int foreignKeyCount = -1;
	
	@CatalogField(ignore = true)
	private String parentCatalogId, greatAncestor, greatDescendant, host;
	
	
	@CatalogField(ignore = true)
	private Class<? extends CatalogEntry> javaClass;

	
	@CatalogField(ignore = true)
	
	private List<? extends FilterDataOrdering> appliedSorts;
	
	@CatalogField(ignore = true)
	
	private List<? extends FilterCriteria> appliedCriteria;
	
	@CatalogField(ignore = true)
	
	protected Map<String, FieldDescriptor> fieldsValues;

	public CatalogDescriptorImpl() {
	}

	public CatalogDescriptorImpl(String catalogId, Class<?> clazz, long numericId, String catalogName, FieldDescriptor... descriptors) {
		this.fieldsValues = new LinkedHashMap<String, FieldDescriptor>();
		if (descriptors != null) {
			for (FieldDescriptor field : descriptors) {
				if (field != null) {
					fieldsValues.put(field.getFieldId(), field);
				}
			}
		}

		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setCatalogId(catalogId);
		setId(numericId);
		setKeyField(CatalogEntry.ID_FIELD);
		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setName(catalogName);
		if (clazz != null)
			setClazz(clazz.getCanonicalName());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long numericId) {
		this.id = numericId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<String> getProperties() {
		return properties;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getCatalogId() {
		return catalogId;
	}

	/**
	 * @param id
	 *            the id to set
	 */

	@Override
	public void setCatalogId(String id) {
		this.catalogId = id;
	}

	/**
	 * @return the clazz
	 */
	@Override
	public String getClazz() {
		return clazz;
	}

	@Override
	public boolean isConsolidated() {
		return consolidated;
	}

	@Override
	public List<String> getContextExpressions() {
		return contextExpressions;
	}

	@Override
	public boolean isRevised() {
		return revised;
	}

	@Override
	public void setRevised(boolean revised) {
		this.revised = revised;
		setVersioned(revised);
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	@Override
	public void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}

	@Override
	public Long getDomain() {
		return domain;
	}

	@Override
	public void setDomain(Long domain) {
		this.domain = domain;
	}

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public int getForeignKeyCount() {
		if (foreignKeyCount == -1) {
			foreignKeyCount = 0;
			Collection<FieldDescriptor> fs = getFieldsValues();
			for (FieldDescriptor f : fs) {
				if (f.isMultiple() || f.isKey() || f.isEphemeral()) {
					foreignKeyCount++;
				}
			}
		}
		return foreignKeyCount;
	}

	@Override
	public boolean isVersioned() {
		return versioned;
	}

	@Override
	public void setVersioned(boolean versioned) {
		this.versioned = versioned;
	}

	@Override
	public List<CatalogActionTrigger> getTriggersValues() {
		if (triggersValues == null) {
			triggersValues = new ArrayList<CatalogActionTrigger>(3);
		}
		return triggersValues;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public void setTriggersValues(List<CatalogActionTrigger> triggersValues) {
		this.triggersValues = triggersValues;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String getCatalog() {
		return CatalogDescriptor.CATALOG_ID;
	}

	@Override
	public String getIdAsString() {
		return String.valueOf(id);
	}

	@Override
	public void setIdAsString(String id) {
		this.id = Long.parseLong(id);
	}

	public void setConsolidated(boolean mergeAncestors) {
		this.consolidated = mergeAncestors;
	}

	/*
	 * auxiliary keys
	 */

	public Long getPeer() {
		return peer;
	}

	public void setPeer(Long peer) {
		this.peer = peer;
	}

	public Long getAncestor() {
		return ancestor;
	}

	public void setAncestor(Long ancestor) {
		parentCatalogId = null;
		this.ancestor = ancestor;
	}

	public List<Long> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Long> triggers) {
		this.triggers = triggers;
	}

	public List<String> getSorts() {
		return sorts;
	}

	public void setSorts(List<String> sorts) {
		this.sorts = sorts;
	}

	public List<String> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<String> criteria) {
		this.criteria = criteria;
	}

	public void setOptimization(Integer cachePolicy) {
		this.optimization = cachePolicy;
	}

	@Override
	public String getKeyField() {
		return CatalogEntry.ID_FIELD;
	}

	@Override
	public String getDescriptiveField() {
		return CatalogEntry.NAME_FIELD;
	}
	
	public List<Long> getFormFields() {
		return formFields;
	}

	public void setFormFields(List<Long> formFields) {
		this.formFields = formFields;
	}

	@Override
	public Set<String> getFields() {
		if (fieldsValues == null) {
			return null;
		}
		return fieldsValues.keySet();
	}

	@Override
	public Collection<FieldDescriptor> getFieldsValues() {
		if (fieldsValues == null) {
			return null;
		}
		return fieldsValues.values();
	}

	public void setContextExpressions(List<String> contextExpressions) {
		this.contextExpressions = contextExpressions;
	}

	public void setFieldsValues(Collection<FieldDescriptor> fieldsValues) {
		if (fieldsValues == null) {
			this.fieldsValues = null;
		} else {
			this.fieldsValues = new LinkedHashMap<String, FieldDescriptor>(fieldsValues.size());
			for (FieldDescriptor f : fieldsValues) {
				this.fieldsValues.put(f.getFieldId(), f);
			}

		}
	}

	public void setForeignKeyCount(int foreignKeyCount) {
		this.foreignKeyCount = foreignKeyCount;
	}

	public void setJavaClass(Class<? extends CatalogEntry> javaClass) {
		this.javaClass = javaClass;
	}

	@Override
	public FieldDescriptor getFieldDescriptor(String id) {
		if (this.fieldsValues == null) {
			return null;
		}

		return this.fieldsValues.get(id);
	}

	@Override
	public Iterator<FieldDescriptor> fieldIterator() {
		return this.fieldsValues == null ? null : this.fieldsValues.values().iterator();
	}

	public void setLocalization(Integer localization) {
		this.localization = localization;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public int getLocalization() {
		return localization;
	}

	public void setLocalization(int localization) {
		this.localization = localization;
	}

	public int getOptimization() {
		return optimization;
	}

	public void setOptimization(int optimization) {
		this.optimization = optimization;
	}

	@Override
	public void putField(FieldDescriptor field) {
		if(this.fieldsValues==null){
			this.fieldsValues= new LinkedHashMap<>();
		}
		this.fieldsValues.put(field.getFieldId(), field);
	}

	@Override
	public void setDescriptiveField(String nameField) {

	}

	@Override
	public void setKeyField(String idField) {
		
	}
	/*
	 * THIS NEEDS SOME CONTEXT
	 */

	public String getParent() {
		return parentCatalogId;
	}

	public void setParent(String parent) {
		this.parentCatalogId = parent;
	}

	public String getGreatAncestor() {
		return greatAncestor;
	}

	public void setGreatAncestor(String greatAncestor) {
		this.greatAncestor = greatAncestor;
	}

	public String getGreatDescendant() {
		return greatDescendant;
	}

	public void setGreatDescendant(String greatDescendant) {
		this.greatDescendant = greatDescendant;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	public void setFieldsValues(Map<String, FieldDescriptor> fieldsValues) {
		this.fieldsValues = fieldsValues;
	}

	public List<? extends FilterDataOrdering> getAppliedSorts() {
		return appliedSorts;
	}

	public void setAppliedSorts(List<? extends FilterDataOrdering> appliedSorts) {
		this.appliedSorts = appliedSorts;
	}

	public List<? extends FilterCriteria> getAppliedCriteria() {
		return appliedCriteria;
	}

	public void setAppliedCriteria(List<? extends FilterCriteria> appliedCriteria) {
		this.appliedCriteria = appliedCriteria;
	}




	public Class<? extends CatalogEntry> getJavaClass() {
		if(javaClass==null){
			try {
				javaClass = (Class<? extends CatalogEntry>) Class.forName(getClazz());
			} catch (ClassNotFoundException e) {
				throw new VegetateException(e.getMessage(),VegetateUserException.INVALID_METADATA,e);
			}
		}
		return javaClass;
	}
}
