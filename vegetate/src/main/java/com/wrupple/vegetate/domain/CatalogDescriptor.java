package com.wrupple.vegetate.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface CatalogDescriptor extends  CatalogEntry, HasCatalogId  {
	public static final String CATALOG_ID = "PersistentCatalogDescriptor";
	String CLOUD_STORAGE_UNIT = "main";
	String LOCAL = "local";
	String LOCAL_KEY_VALUE_PAIR = "index";
	String LOCAL_CACHE = "volatile";
	
	String CLOUD_CACHE = "quick";
	String SECURE = "secure";
	
	/* 
	 * NOT VISIBLE TO CLIENTS
	 * (non-Javadoc)
	 * @see com.wrupple.vegetate.domain.CatalogKey#getId()
	 */
	public Long getId();
	/**
	 * @return the clazz
	 */
	public String getClazz();
	
	public String getParent();
	
	public String getGreatAncestor();

	public String getCatalogId();
	
	/**
	 * @return url (also used as peerManager pseudo-key)
	 */
	public String getHost();
	
	/**
	 * indicates the client what language or vendor specific implementation to
	 * use when interacting with this catalog.
	 * 
	 * @return storage service
	 */
	public int getStorage();

	public int getOptimization();

	public int getLocalization();
	
	/**
	 * @return the keyField
	 */
	public String getKeyField();

	
	/**
	 * @return The ID of the human-readable most descriptive field of this
	 *         catalog, by default it's the keyField, but this may not always be
	 *         the most intuitive field.
	 */
	public String getDescriptiveField();
	
	/**
	 * @return a set of all the ids of all fields
	 */
	public Collection<String> getFields();
	
	public Collection<FieldDescriptor> getFieldsValues();
	/**
	 * @param id
	 *            the Machine Readable id for the field
	 * @return
	 */
	public FieldDescriptor getFieldDescriptor(String id);

	public List<CatalogActionTrigger> getTriggersValues();

	public boolean isRevised();
	public void setRevised(boolean b);
	public boolean isVersioned();
	public void setVersioned(boolean b);
	
	/**
	 * 
	 * @return true is this catalog consolidates all ancestry fields into a
	 *         single entity, rather than scathering data along the ancestry
	 *         tree
	 */
	public boolean isConsolidated();


	public Iterator<FieldDescriptor> fieldIterator();

	public List<String> getProperties();

	public List<FilterDataOrdering> getAppliedSorts();

	public List<? extends FilterCriteria> getAppliedCriteria();

	/**
	 * CatalogEvaluationDelegate will evaluate each of these statements while
	 * initializing and evaluation context for this catalog.
	 * 
	 * Posible use is to define functions specific to this catalog
	 * 
	 * @return
	 */
	public List<String> getContextExpressions();


	public int getForeignKeyCount();


	void setHost(String host);

	public void putField(FieldDescriptor field);
	
	public void setDescriptiveField(String nameField);

	public void setKeyField(String idField);

	public Class<? extends CatalogEntry> getJavaClass();
	public void setAppliedSorts(List<? extends FilterDataOrdering> sorts);

	
}
