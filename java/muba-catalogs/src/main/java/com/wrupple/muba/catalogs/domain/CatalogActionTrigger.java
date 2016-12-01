package com.wrupple.muba.catalogs.domain;

import java.util.List;
import java.util.Map;

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
	
	public boolean isAdvice();


	Map<String, String> getParsedProperties(List<String> rawProperties, Map context);


	void setParsedProperties(Map<String, String> parsed, List<String> rawProperties, Map context);

}
