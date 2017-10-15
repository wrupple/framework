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
    String ADVISE_FIELD = "advice";

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
