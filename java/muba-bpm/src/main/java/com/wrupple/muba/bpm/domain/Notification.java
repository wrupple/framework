package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.catalogs.domain.Location;


/**
 * @author japi
 *
 */
public interface Notification extends ProcessRequest,ImplicitIntent{

	String CATALOG = "Notification";

    Long getSource();
	
	String getSourceType();

	CatalogEntry getSourceValue();

    Long getTarget();

    String getTargetType();

    CatalogEntry getTargetValue();
	

}
