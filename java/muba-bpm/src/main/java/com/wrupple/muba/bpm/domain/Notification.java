package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ImplicitIntent;


/**
 * @author japi
 *
 */
public interface Notification extends BusinessEvent,ImplicitIntent{

	String CATALOG = "Notification";

    Long getSource();
	
	String getSourceType();

	CatalogEntry getSourceValue();

    Long getTarget();

    String getTargetType();

    CatalogEntry getTargetValue();
	

}
