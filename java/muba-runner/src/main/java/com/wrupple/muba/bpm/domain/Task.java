package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.catalogs.domain.CatalogActionConstraint;
import com.wrupple.muba.event.domain.Service;
import com.wrupple.muba.event.domain.reserved.HasOutput;

import java.util.List;

public interface Task extends HasOutput, Service, CatalogActionConstraint, Job {

    String CATALOG = "Task";
	String COMMAND_FIELD = "machineTaskCommandName";
	// output is the exit place
	String NAVIGATE_COMMAND = "navigate";
	// output is detail of selected place
	String SELECT_COMMAND = "select";


    List<Long> getUserActions();


	/**
	 * 
	 * @return
	 */
    List<Long> getToolbars();


    List<? extends TaskToolbarDescriptor> getToolbarsValues();


    void setDistinguishedName(String driverPick);

}
