package com.wrupple.muba.event.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.event.domain.annotations.CatalogKey;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.InheritanceTree;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CatalogDescriptorImpl extends AbstractContractDescriptor implements CatalogDescriptor {

	private static final long serialVersionUID = 7222404658673284250L;

	@CatalogKey(foreignCatalog = Host.CATALOG)
	private Long peer;

	@CatalogKey(foreignCatalog = CatalogDescriptor.CATALOG_ID)
	@InheritanceTree
	private Long parent;

	private boolean typed;

	private Class<? extends CatalogEntry> clazz;

	@CatalogFieldValues(defaultValueOptions = { MAIN_STORAGE_UNIT, QUICK_STORAGE_UNIT, LOCAL, LOCAL_KEY_VALUE_PAIR,
			LOCAL_CACHE, MAIN_CACHE, SECURE })
	private int storage;

	@CatalogFieldValues(defaultValueOptions = { CONSOLIDATED, DistributiedLocalizedEntry.CATALOG})
	private int localization;

	@CatalogFieldValues(defaultValueOptions = { CatalogActionRequest.FULL_CACHE, CatalogActionRequest.QUERY_CACHE,
			CatalogActionRequest.NO_CACHE })
	private int optimization /* cachePolicy */;

	private boolean consolidated, revised,
			versioned/*
						 * persisted with jdo strategy, no namespace, saving
						 * domain data as a field
						 */;

	@CatalogKey(foreignCatalog = FieldDescriptor.CATALOG_ID)
	private List<Long> fields;
	
	@CatalogKey(foreignCatalog = Constraint.CATALOG_ID)
	private List<Long> constraints;

	@CatalogField(ignore = true)
	@CatalogValue(foreignCatalog = Constraint.CATALOG_ID)
	private List<Constraint> constraintsValues;

	private List<String> sorts, criteria;

	@CatalogField(ignore = true)
	private int foreignKeyCount = -1;

	@CatalogField(ignore = true)
	private String greatAncestor, greatDescendant, host;

	@CatalogField(ignore = true)
	private Class<? extends CatalogEntry> javaClass;

	@CatalogField(ignore = true)

	private List<FilterDataOrdering> appliedSorts;

	@CatalogField(ignore = true)
	private List<? extends FilterCriteria> appliedCriteria;

	@CatalogField(ignore = true)
	@CatalogValue(foreignCatalog = FieldDescriptor.CATALOG_ID)
	protected Map<String, FieldDescriptor> fieldsValues;

	@CatalogField(filterable = true)
	private String distinguishedName;

	private Long version;

	public CatalogDescriptorImpl() {
	}

	public CatalogDescriptorImpl(String catalogId, Class<? extends CatalogEntry> clazz, long numericId, String catalogName, Long parentId,
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
		setConsolidated(parentId == null);
		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setDistinguishedName(catalogId);
		setId(numericId);
		setKeyField(CatalogEntry.ID_FIELD);
		setDescriptiveField(CatalogEntry.NAME_FIELD);
		setName(catalogName);
		setClazz(clazz);
		if (clazz != null)
			this.setTyped(PersistentCatalogEntity.class.equals(clazz));
	}

	public List<Long> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Long> constraints) {
		this.constraints = constraints;
	}

	public List<Constraint> getConstraintsValues() {
		return constraintsValues;
	}

	public void setConstraintsValues(List<Constraint> constraintsValues) {
		this.constraintsValues = constraintsValues;
	}

	@Override
	public final String getDistinguishedName() {
		return distinguishedName;
	}

	public final void setDistinguishedName(String catalogId) {
		this.distinguishedName = catalogId;
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
	public String toString() {
		return "Catalog["+ distinguishedName + "]";
	}

	@Override
	public Collection<String> getFieldsIds() {
		return fieldsValues == null ? null : fieldsValues.keySet();
	}

	@Override
	public String getCatalogType() {
		return CatalogDescriptor.CATALOG_ID;
	}


	public boolean isTyped() {
		return typed;
	}

	public void setTyped(boolean typed) {
		this.typed = typed;
	}

	public Class<? extends CatalogEntry> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends CatalogEntry> clazz) {
		this.clazz = clazz;
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


	@Override
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

    @Override
    public CatalogDescriptor getParentValue() {
        return null;
    }

    @Override
    public CatalogDescriptor getRootAncestor() {
        return null;
    }
}
