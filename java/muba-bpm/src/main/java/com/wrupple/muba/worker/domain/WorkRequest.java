package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.Intent;


/**
 * @author japi
 *
 */
public interface WorkRequest extends BusinessIntent,Intent {

String CATALOG = "WorkRequest";
	/*
    Long getSource();
	
	String getSourceType();

	CatalogEntry getSourceValue();

    Long getTarget();

    String getTargetType();

    CatalogEntry getTargetValue();
	*/

}
