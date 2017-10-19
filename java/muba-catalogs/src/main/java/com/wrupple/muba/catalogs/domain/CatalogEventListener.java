package com.wrupple.muba.catalogs.domain;

import java.util.List;
import java.util.Map;

/**
 * @author japi
 *
 */
public interface CatalogEventListener extends UserDefinedCatalogJob {
	String CATALOG = "CatalogEventListener";
	String ACTION_FIELD="action";

    Long getAction();

	
	/**
	 * used by ScheduledTasksServlet
	 * 
	 * @return
	 */
	String getSystemEvent();
	
	public Boolean isAdvice();


}
