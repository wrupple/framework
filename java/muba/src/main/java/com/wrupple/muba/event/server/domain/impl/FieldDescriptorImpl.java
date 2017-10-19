package com.wrupple.muba.event.server.domain.impl;

import java.util.List;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.annotations.*;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

/**
 * 
 * 
 * @author japi
 * 
 */
public class FieldDescriptorImpl extends CatalogEntryImpl implements FieldDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2502990328355666825L;
	private Long id;
	@ForeignKey(foreignCatalog=CatalogDescriptor.CATALOG_ID)
	private Long foreignCatalog;
	@CatalogFieldValues(defaultValueOptions={"Default"})
	int dataType;
	private boolean masked = false, multiple = false, sortable = false, ephemeral = false, filterable = false, createable = true, writeable = true, detailable = true,
			summary = true, localized = false, key = false,hardKey=false;

	private String catalog;
	private String fieldId,description,help, defaultValue, command;
	

	private List<String> defaultValueOptions, properties, sentence;

	@ForeignKey(foreignCatalog=Constraint.CATALOG_ID)
	private List<Long> constraints;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Constraint.CATALOG_ID)
	private List<Constraint> constraintsValues;
	
	
	public FieldDescriptorImpl() {
	}
	
	public  FieldDescriptorImpl makeDefault(String id,
			String name,  Integer dataType) {
		setKey(false);
		setCreateable(true);
		setDataType(dataType);
		setDetailable(true);
		setEphemeral(false);
		setFilterable(true);
		setFieldId(id);
		setMultiple(false);
		setName(name);
		setSortable(false);
		setSummary(true);
		setWriteable(true);
		return this;
	}

	public FieldDescriptorImpl makeKey(String id,
			String name, String foreign_catalog, boolean multiple) {
		setCreateable(true);
		setDataType(CatalogEntry.INTEGER_DATA_TYPE);
		setDetailable(true);
		setWriteable(false);
		setEphemeral(false);
		setFilterable(true);
		setKey(true);
		setCatalog(foreign_catalog);
		setFieldId(id);
		setMultiple(multiple);
		setName(name);
		setSortable(false);
		setSummary(true);
		setWriteable(true);
		return this;
	}

	public FieldDescriptorImpl makeEphemeral(String id,
			String name, String foreign_catalog, boolean multiple) {
		setCreateable(false);
		setDataType(0);
		setDetailable(true);
		setWriteable(false);
		setEphemeral(true);
		setFilterable(false);
		setKey(false);
		setCatalog(foreign_catalog);
		setFieldId(id);
		setMultiple(multiple);
		setName(name);
		setSortable(false);
		setSummary(true);
		setWriteable(false);
		return this;
	}
	

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public int getDataType() {
		return dataType;
	}

	public List<String> getSentence() {
		return sentence;
	}

	public void setSentence(List<String> sentence) {
		this.sentence = sentence;
	}

	/**
	 * @return the value used to fill out this entry
	 */
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public List<String> getDefaultValueOptions() {
		return defaultValueOptions;
	}


	/**
	 * @return the machine readable name of this field (identicall to the java
	 *         property name)
	 */
	@Override
	public String getFieldId() {
		return fieldId;
	}




	/**
	 * @return the createable
	 */
	@Override
	public boolean isCreateable() {
		return createable;
	}

	/**
	 * @return the detailable
	 */
	@Override
	public boolean isDetailable() {
		return detailable;
	}

	/**
	 * @return the editable
	 */
	@Override
	public boolean isWriteable() {
		return writeable;
	}

	@Override
	public boolean isEphemeral() {
		return ephemeral;
	}

	/**
	 * @return the filterable
	 */
	@Override
	public boolean isFilterable() {
		return filterable;
	}

	/**
	 * @return true if this field is a foreign key values AS IS, pointing to
	 *         another catalog's entry
	 */
	@Override
	public boolean isKey() {
		return key;
	}

	@Override
	public boolean isMultiple() {
		return multiple;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * @return the summary
	 */
	@Override
	public boolean isSummary() {
		return summary;
	}

	public void setFieldId(String id) {
		this.fieldId = id;
	}
	

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
	

	public void setDefaultValueOptions(List<String> defaultValueOptions) {
		this.defaultValueOptions = defaultValueOptions;
	}

	public void setCatalog(String foreignCatalog) {
		this.catalog = foreignCatalog;
	}
	@Override
	public String getCatalog() {
		return catalog;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public void setEphemeral(boolean ephemeral) {
		this.ephemeral = ephemeral;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	public void setCreateable(boolean createable) {
		this.createable = createable;
	}

	public void setWriteable(boolean writeable) {
		this.writeable = writeable;
	}

	public void setDetailable(boolean detailable) {
		this.detailable = detailable;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}


	public boolean isLocalized() {
		return localized;
	}

	public void setLocalized(boolean localized) {
		this.localized = localized;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}


	@Override
	public List<Constraint> getConstraintsValues() {
		return constraintsValues;
	}
	
	public void setConstraintsValues(List<? extends Constraint> consta){
		this.constraintsValues=(List<Constraint>) consta;
	}

	@Override
	public boolean alwaysRecalculate() {
		return false;
	}

	public Long getForeignCatalog() {
		return foreignCatalog;
	}

	public void setForeignCatalog(Long foreignCatalog) {
		this.foreignCatalog = foreignCatalog;
	}



	@Override
	public String getCatalogType() {
		return CATALOG_ID;
	}


	public List<Long> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Long> constraints) {
		this.constraints = constraints;
	}

	

	@Override
	public String toString() {
		return "FieldDescriptor[" + fieldId + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public boolean isHardKey() {
		return hardKey;
	}

	public void setHardKey(boolean hardKey) {
		this.hardKey = hardKey;
	}

	public boolean isMasked() {
		return masked;
	}

	public void setMasked(boolean masked) {
		this.masked = masked;
	}




}