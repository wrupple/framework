package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;

/**
 * the task of the process currently being worked on, the type of catalog used
 * to persist activity state ( Context ) and the particular entry this
 * excecution represents. Used to recover and record Activities such as those
 * subject to billing
 * 
 * @author japi
 *
 */
public interface HumanTaskData extends HasCatalogId, HasEntryId {
	String CATALOG = "HumanTaskData";
	String FIELD =  "field";
	String FINAL_VALUE = "finalValue";
	String INITIAL_VALUE ="initialValue"; 
	
	String getTask();

	String getProcess();
}
