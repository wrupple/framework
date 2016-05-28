package com.wrupple.muba.cms.domain;

import java.util.List;

import com.wrupple.muba.catalogs.domain.HasVanityId;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasCatalogId;

public interface ProcessTaskDescriptor extends CatalogEntry,HasCatalogId,HasVanityId{
	
	String CATALOG = "TaskDescriptor";
	String COMMAND_FIELD = "machineTaskCommandName";
	// output is the exit place
	String NAVIGATE_COMMAND = "navigate";
	// output is detail of selected place
	String SELECT_COMMAND = "select";
	
	String getTransactionType();
	
	
	String getMachineTaskCommandName();
	
	public List<Long> getUserActions();
	
	/**
	 * 
	 * @return
	 */
	public List<Long> getToolbars();
	
	
	public List<? extends TaskToolbarDescriptor> getToolbarsValues();
	
	public List<String> getProperties();

	List<String> getUrlTokens();

}
