package com.wrupple.muba.catalogs.server.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wrupple.muba.bootstrap.domain.AbstractContractDescriptor;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;
import com.wrupple.muba.bootstrap.domain.KnownException;
import com.wrupple.muba.bootstrap.domain.KnownExceptionImpl;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.CatalogField;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;
import com.wrupple.muba.catalogs.domain.annotations.ConsistentFields;
import com.wrupple.muba.catalogs.domain.annotations.InheritanceTree;
import com.wrupple.muba.catalogs.server.chain.command.I18nProcessing;

public class CatalogDescriptorImpl extends AbstractContractDescriptor implements CatalogDescriptor {

	private static final long serialVersionUID = 7222404658673284250L;

	@CatalogKey(foreignCatalog = CatalogPeer.CATALOG)
	private Long peer;

	@CatalogKey(foreignCatalog = CatalogDescriptor.CATALOG_ID)
	@InheritanceTree
	private Long parent;

	@CatalogFieldValues(defaultValueOptions = { "Map" })
	private String clazz;

	@CatalogFieldValues(defaultValueOptions = { MAIN_STORAGE_UNIT, QUICK_STORAGE_UNIT, LOCAL, LOCAL_KEY_VALUE_PAIR,
			LOCAL_CACHE, MAIN_CACHE, SECURE })
	private int storage;

	@CatalogFieldValues(defaultValueOptions = { I18nProcessing.CONSOLIDATED, I18nProcessing.DISTRIBUTED })
	private int localization;

	@CatalogFieldValues(defaultValueOptions = { CatalogActionRequest.FULL_CACHE, CatalogActionRequest.QUERY_CACHE,
			CatalogActionRequest.NO_CACHE })
	private int optimization /* cachePolicy */;

	private boolean consolidated, revised,
			versioned/*
						 * persisted with jdo strategy, no namespace, saving
						 * domain data as a field
						 */;

	@ConsistentFields
	@CatalogKey(foreignCatalog = FieldDescriptor.CATALOG_ID)
	private List<Long> fields;

	@CatalogKey(foreignCatalog = CatalogActionTrigger.CATALOG)
	private List<Long> triggers;

	private List<String> sorts, criteria;

	@CatalogField(ephemeral = true)
	private List<CatalogActionTrigger> triggersValues;

	@CatalogField(ignore = true)
	private int foreignKeyCount = -1;

	@CatalogField(ignore = true)
	private String  greatAncestor, greatDescendant, host;

	@CatalogField(ignore = true)
	private Class<? extends CatalogEntry> javaClass;

	@CatalogField(ignore = true)

	private List<FilterDataOrdering> appliedSorts;

	@CatalogField(ignore = true)
	private List<? extends FilterCriteria> appliedCriteria;

	@CatalogField(ignore = true)
	@CatalogValue(foreignCatalog=FieldDescriptor.CATALOG_ID)
	protected Map<String, FieldDescriptor> fieldsValues;

	@CatalogField(filterable = true)
	private String catalog;

	public CatalogDescriptorImpl() {
	}

	public CatalogDescriptorImpl(String catalogId, Class<?> clazz, long numericId, String catalogName, Long parentId,
			FieldDescriptor... descriptors) {
		this.fieldsValues = new LinkedHashMap<String, FieldDescriptor>();
		if (descriptors != null) {
			for (FieldDescriptor field : descriptors) {
				if (field != null) {
					fieldsValues.put(field.getFieldId(), field);
				}
			}
		}
		setParent(parentId);
		setConsolidated(parentId==null);
		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setCatalog(catalogId);
		setId(numericId);
		setKeyField(CatalogEntry.ID_FIELD);
		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setName(catalogName);
		if (clazz != null)
			setClazz(clazz.getCanonicalName());
	}

	public final String getCatalog() {
		return catalog;
	}

	public final void setCatalog(String catalogId) {
		this.catalog = catalogId;
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
	public boolean isRevised() {
		return revised;
	}

	@Override
	public void setRevised(boolean revised) {
		this.revised = revised;
		setVersioned(revised);
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
		return triggersValues;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	@Override
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public void setTriggersValues(List<CatalogActionTrigger> triggersValues) {
		this.triggersValues = triggersValues;
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

	public List<Long> getFields() {
		return fields;
	}

	public void setFields(List<Long> formFields) {
		this.fields = formFields;
	}

	@Override
	public Collection<FieldDescriptor> getFieldsValues() {
		if (fieldsValues == null) {
			return null;
		}
		return fieldsValues.values();
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
		if (this.fieldsValues == null) {
			this.fieldsValues = new LinkedHashMap<String, FieldDescriptor>();
		}
		this.fieldsValues.put(field.getFieldId(), field);
	}

	@Override
	public void setDescriptiveField(String nameField) {

	}

	/*
	 * THIS NEEDS SOME CONTEXT
	 */

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
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

	public List<FilterDataOrdering> getAppliedSorts() {
		return appliedSorts;
	}

	public void setAppliedSorts(List<? extends FilterDataOrdering> appliedSorts) {
		this.appliedSorts = (List<FilterDataOrdering>) appliedSorts;
	}

	public List<? extends FilterCriteria> getAppliedCriteria() {
		return appliedCriteria;
	}

	public void setAppliedCriteria(List<? extends FilterCriteria> appliedCriteria) {
		this.appliedCriteria = appliedCriteria;
	}

	@Override
	public Class<? extends CatalogEntry> getJavaClass() {
		if (javaClass == null) {
			try {
				javaClass = (Class<? extends CatalogEntry>) Class.forName(getClazz());
			} catch (ClassNotFoundException e) {
				throw new KnownExceptionImpl(e.getMessage(), KnownException.UNAVAILABLE_METADATA, e);
			}
		}
		return javaClass;
	}


	@Override
	public String toString() {
		return "CatalogDescriptorImpl [catalog=" + catalog +", fields=" + fields + ", fieldsValues=" + fieldsValues + "]";
	}

	@Override
	public Collection<String> getFieldsIds() {
		return fieldsValues==null?null :fieldsValues.keySet();
	}
	@Override
	public String getCatalogType() {
		return CatalogDescriptor.CATALOG_ID;
	}

	@Override
	public void addTrigger(CatalogActionTrigger t) {
		if (triggersValues == null) {
			triggersValues = new ArrayList<CatalogActionTrigger>(3);
		}
		triggersValues.add(t);
	}

}