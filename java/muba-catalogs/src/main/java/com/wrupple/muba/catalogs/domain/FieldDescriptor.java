package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasDescription;
import com.wrupple.muba.event.domain.reserved.HasFieldId;
import com.wrupple.muba.event.domain.reserved.HasProperties;


/**
 * 
 * 
 * @author japi
 * 
 */
public interface FieldDescriptor extends CatalogEntry,HasFieldId,HasProperties,HasCatalogId ,HasDescription,HasConstrains{
	public static final String CATALOG_ID = "FieldDescriptor";


	
	/**
	 * @return the summary
	 */
	public boolean isSummary();


	/**
	 * @return the type of data that this field holds
	 */
	public int getDataType();

	/**
	 * @return the widget
	 */
	public String getWidget();

	/**
	 * @return can this field be sorted?
	 */
	public boolean isSortable();

	/**
	 * @return the filterable
	 */
	public boolean isFilterable();

	/**
	 * @return the createable
	 */
	public boolean isCreateable() ;

	/**
	 * @return the writeable
	 */
	public boolean isWriteable() ;


	/**
	 * @return the detailable
	 */
	public boolean isDetailable() ;



	/**
	 * @return the value used to fill out this entry 
	 */
	public String getDefaultValue() ;

	/**
	 * @return
	 */
	public String getCatalog();

	/**
	 * @return
	 */
	public boolean isKey();
	
	/**
	 * @return
	 */
	public boolean isInherited();

	public void setInherited(boolean asInherited);
	/**
	 * @return The id of the catalog in the hierarchy that defines this field
	 */
	public String getOwnerCatalogId();
	

	public void setOwnerCatalogId(String catalogId);
	
	public String getHelp() ;
	
	/**
	 * @return
	 */
	public boolean isEphemeral();

	public boolean isMultiple();
	
	public String getCommand();

	public boolean isLocalized();
	
	public String getFormula();

	/**
	 * 
	 * @return
	 */
	public List<String> getDefaultValueOptions();
	
	public boolean alwaysRecalculate();

	public void setWriteable(boolean b);

	/**
	 * @return true if deletion of the foreign entry implies the deletion of all entries referencing it through this field
	 */
	public boolean isHardKey();
	
	/**
	 * @return masked fields never leave the server
	 */
	public boolean isMasked();


	public void setDefaultValueOptions(List<String> asList);
}
