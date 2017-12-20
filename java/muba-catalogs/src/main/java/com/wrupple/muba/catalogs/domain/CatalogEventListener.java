package com.wrupple.muba.catalogs.domain;

/**
 * @author japi
 *
 */
public interface CatalogEventListener extends UserDefinedCatalogActionConstraint {
    String CATALOG = "CatalogEventListener";
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
