package com.wrupple.muba.catalogs.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;
import com.wrupple.muba.bootstrap.domain.reserved.HasParent;

public interface CatalogDescriptor extends  CatalogEntry, ContractDescriptor ,HasParent<Long> {
	public static final String CATALOG_ID = "CatalogDescriptor";
	String MAIN_STORAGE_UNIT = "main";
	String QUICK_STORAGE_UNIT = "quick";
	String LOCAL = "local";
	String LOCAL_KEY_VALUE_PAIR = "dictionary";
	String LOCAL_CACHE = "volatile";
	
	String MAIN_CACHE = "index";
	String SECURE = "secure";
	
	
	public String getGreatAncestor();

	public String getCatalog();
	
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


	public List<FilterDataOrdering> getAppliedSorts();

	public List<? extends FilterCriteria> getAppliedCriteria();

	


	public int getForeignKeyCount();


	void setHost(String host);

	public void putField(FieldDescriptor field);
	
	public void setDescriptiveField(String nameField);

	public void setKeyField(String idField);

	public Class<? extends CatalogEntry> getJavaClass();
	public void setAppliedSorts(List<? extends FilterDataOrdering> sorts);

	public void addTrigger(CatalogActionTrigger beforeIndexedTreeCreate);

	public void setGreatAncestor(String greatAncestor);
}
