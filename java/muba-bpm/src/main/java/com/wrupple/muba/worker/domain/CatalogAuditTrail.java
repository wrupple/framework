package com.wrupple.muba.worker.domain;


import com.wrupple.muba.event.domain.ManagedObject;

public interface CatalogAuditTrail extends ManagedObject {
	String CATALOG = "WruppleCatalogAuditTrail";

	Long getAccountId();

	String getTargetActionId();

	String getTargetEntry();

	String getTargetCatalogId();

	String getParameters();
	
	Long getClient();
	
	Long getProcess();
	
	Long getTask();
	
}
