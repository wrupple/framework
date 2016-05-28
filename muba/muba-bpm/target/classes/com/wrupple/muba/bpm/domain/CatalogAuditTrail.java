package com.wrupple.muba.bpm.domain;


public interface CatalogAuditTrail extends ManagedObject{
	String CATALOG = "WruppleCatalogAuditTrail";

	Long getAccountId();

	String getTargetActionId();

	String getTargetEntryId();

	String getTargetCatalogId();

	String getParameters();
	
	Long getClient();
	
	Long getProcess();
	
	Long getTask();
	
}
