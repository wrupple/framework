package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogJob;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasOutput;

public interface Task extends HasOutput,HasDistinguishedName,CatalogJob {
	
	String CATALOG = "TaskDescriptor";
	String COMMAND_FIELD = "machineTaskCommandName";
	// output is the exit place
	String NAVIGATE_COMMAND = "navigate";
	// output is detail of selected place
	String SELECT_COMMAND = "select";
	final String CONSTRAINT = "constraint";

	
	
	public List<Long> getUserActions();


	/**
	 * 
	 * @return
	 */
	public List<Long> getToolbars();
	
	
	public List<? extends TaskToolbarDescriptor> getToolbarsValues();
	
	List<String> getUrlTokens();

    void setDistinguishedName(String driverPick);

}
