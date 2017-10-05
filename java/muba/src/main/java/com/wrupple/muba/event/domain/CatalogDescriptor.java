package com.wrupple.muba.event.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.wrupple.muba.event.domain.reserved.HasConstrains;
import com.wrupple.muba.event.domain.reserved.HasParentValue;
import com.wrupple.muba.event.domain.reserved.Versioned;

public interface CatalogDescriptor extends  CatalogEntry, ContractDescriptor ,HasParentValue<Long,CatalogDescriptor>,Versioned,HasConstrains {
	public static final String CATALOG_ID = "CatalogDescriptor";
	String MAIN_STORAGE_UNIT = "main";
	String QUICK_STORAGE_UNIT = "quick";
	String LOCAL = "local";
	String LOCAL_KEY_VALUE_PAIR = "dictionary";
	String LOCAL_CACHE = "volatile";
	
	String MAIN_CACHE = "index";
	String SECURE = "secure";
    String CONSOLIDATED =  "MONOLITIC";


    public String getGreatAncestor();

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

	/**
	 * @return cache or other resource saving strategy
	 */
	public int getOptimization();

	/**
	 * @return strategy to store locale dependent values
	 */
	public int getLocalization();
	

	public Collection<FieldDescriptor> getFieldsValues();
	
	/**
	 * @param id
	 *            the Machine Readable id for the field
	 * @return
	 */
	public FieldDescriptor getFieldDescriptor(String id);

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


	public List<FilterDataOrdering> getAppliedSorts();

	public List<? extends FilterCriteria> getAppliedCriteria();

	


	public int getForeignKeyCount();


	void setHost(String host);

	public void putField(FieldDescriptor field);
	
	public void setDescriptiveField(String nameField);

	public void setKeyField(String idField);

	public void setAppliedSorts(List<? extends FilterDataOrdering> sorts);


	public void setGreatAncestor(String greatAncestor);
}