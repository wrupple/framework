package com.wrupple.muba.catalogs.domain;

/**
 * @author japi
 *
 */
public interface CatalogContractListener extends UserDefinedCatalogActionConstraint {
    String CATALOG = "CatalogContractListener";
	String ACTION_FIELD="action";

    Long getAction();

	
	/**
	 * used by ScheduledTasksServlet
	 * 
	 * @return
	 */
	String getSystemEvent();

    Boolean isAdvice();


}
