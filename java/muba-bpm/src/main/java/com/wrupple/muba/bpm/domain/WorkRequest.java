package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ImplicitIntent;


/**
 * @author japi
 *
 */
public interface WorkRequest extends BusinessIntent,ImplicitIntent {

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
