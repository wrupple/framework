package com.wrupple.vegetate.domain;

/**
 * @author japi
 *
 */
public interface CatalogActionTrigger extends CatalogTrigger {
	String CATALOG = "CatalogActionTrigger";

	int getAction();

	
	/**
	 * used by ScheduledTasksServlet
	 * 
	 * @return
	 */
	String getSystemEvent();
	
	public boolean isBefore();

}
