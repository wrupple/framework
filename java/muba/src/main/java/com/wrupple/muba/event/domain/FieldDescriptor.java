package com.wrupple.muba.event.domain;

import java.util.List;

import com.wrupple.muba.event.domain.reserved.*;


/**
 * 
 * 
 * @author japi
 * 
 */
public interface FieldDescriptor extends CatalogEntry,HasFieldId,HasProperties,HasCatalogId ,HasDescription,HasConstrains {
	String CATALOG_ID = "FieldDescriptor";

	/**static final
	 * @return the summary
	 */
	boolean isSummary();


	/**
	 * @return the type of data that this field holds
	 */
	int getDataType();

	/**
	 * @return can this field be sorted?
	 */
	boolean isSortable();

	/**
	 * @return the filterable
	 */
	boolean isFilterable();

	/**
	 * @return the createable
	 */
	boolean isCreateable() ;

	/**
	 * @return the writeable
	 */
	boolean isWriteable() ;


	/**
	 * @return the detailable
	 */
	boolean isDetailable() ;



	/**
	 * @return the value used to fill out this entry 
	 */
	String getDefaultValue() ;

	/**
	 * @return
	 */
	String getCatalog();

	/**
	 * @return
	 */
	boolean isKey();

	
	String getHelp() ;
	
	/**
	 * @return
	 */
	boolean isEphemeral();

	boolean isMultiple();
	
	String getCommand();

	boolean isLocalized();


	/**
	 * 
	 * @return
	 */
	List<String> getDefaultValueOptions();
	
	boolean alwaysRecalculate();

	void setWriteable(boolean b);

	/**
	 * @return true if deletion of the foreign entry implies the deletion of all entries referencing it through this field
	 */
	boolean isHardKey();
	
	/**
	 * @return masked fields never leave the server
	 */
	boolean isMasked();


	void setDefaultValueOptions(List<String> asList);

	List<String> getSentence();
}
