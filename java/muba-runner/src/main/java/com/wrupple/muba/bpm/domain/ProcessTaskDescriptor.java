package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface ProcessTaskDescriptor extends CatalogEntry,HasCatalogId,HasDistinguishedName,HasProperties{
	
	String CATALOG = "TaskDescriptor";
	String COMMAND_FIELD = "machineTaskCommandName";
	// output is the exit place
	String NAVIGATE_COMMAND = "navigate";
	// output is detail of selected place
	String SELECT_COMMAND = "select";
	final String CONSTRAINT = "constraint";

	List<String> getSentence();
	
	
	public List<Long> getUserActions();

	String getTransactionType();
	
	/**
	 * @return saveToField
	 */
	String getProducedField();
	/**
	 * 
	 * @return
	 */
	public List<Long> getToolbars();
	
	
	public List<? extends TaskToolbarDescriptor> getToolbarsValues();
	
	List<String> getUrlTokens();

    void setDistinguishedName(String driverPick);

	void setTransactionType(String selectCommand);
}
