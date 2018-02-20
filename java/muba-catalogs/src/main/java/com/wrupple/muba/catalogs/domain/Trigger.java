package com.wrupple.muba.catalogs.domain;

/**
 * @author japi
 *
 */
public interface Trigger extends UserDefinedCatalogActionConstraint {
    String CATALOG = "Trigger";
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
