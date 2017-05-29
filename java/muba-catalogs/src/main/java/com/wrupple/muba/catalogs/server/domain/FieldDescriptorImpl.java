package com.wrupple.muba.catalogs.server.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.CatalogField;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldWidget;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;

/**
 * 
 * 
 * @author japi
 * 
 */
public class FieldDescriptorImpl implements FieldDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2502990328355666825L;
	private Long id;
	@CatalogKey(foreignCatalog=CatalogDescriptor.CATALOG_ID)
	private Long foreignCatalog;
	@CatalogFieldValues(defaultValueOptions={"Default"})
	int dataType;
	private boolean masked = false,anonymouslyVisible =false, multiple = false, sortable = false, ephemeral = false, filterable = false, createable = true, writeable = true, detailable = true,
			summary = true, inherited = false, localized = false, key = false,hardKey=false;
	
	@CatalogFieldWidget(widget="Widget")
	private String widget;
	@CatalogFieldWidget(widget="catalogPicker")
	private String catalog;
	private String fieldId,description,help, name, defaultValue, command, formula;

	@CatalogField(ignore=true)
	private String ownerCatalogId;
	

	private List<String> defaultValueOptions, properties;

	@CatalogKey(foreignCatalog=Constraint.CATALOG_ID)
	private List<Long> constraints;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Constraint.CATALOG_ID)
	private List<Constraint> constraintsValues;
	
	
	public FieldDescriptorImpl() {
	}
	
	public  FieldDescriptorImpl makeDefault(String id,
			String name, String widget, Integer dataType) {
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
		setWidget(widget);
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
		setWidget("genericValue");
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
		setWidget("genericValue");
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

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
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
	 * @return the name of the field
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the widget
	 */
	@Override
	public String getWidget() {
		return widget;
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
	public void setName(String name) {
		this.name = name;
	}

	public void setWidget(String widget) {
		this.widget = widget;
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

	@Override
	public boolean isInherited() {
		return inherited;
	}

	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

	public String getOwnerCatalogId() {
		return ownerCatalogId;
	}

	public void setOwnerCatalogId(String ownerCatalogId) {
		this.ownerCatalogId = ownerCatalogId;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatalogType() {
		return CATALOG_ID;
	}


	@Override
	public void setDomain(Long domain) {
		
	}

	@Override
	public Long getDomain() {
		return CatalogEntry.WRUPPLE_ID;
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {
		this.anonymouslyVisible=p;
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