package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.Invocation;
import com.wrupple.muba.event.domain.reserved.HasResult;


/**
 * @author japi
 *
 */
public interface Request extends Invocation,HasResult<Object> {

    String CATALOG = "Request";


    void setError(Exception e);

	/*
    Long getSource();
	
	String getSourceType();

	CatalogEntry getSourceValue();

    Long getTarget();

    String getTargetType();

    CatalogEntry getTargetValue();
	*/

}
