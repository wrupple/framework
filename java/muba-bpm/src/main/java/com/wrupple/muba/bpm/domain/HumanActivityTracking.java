package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.reserved.HasParentValue;

public interface HumanActivityTracking extends HasParentValue<String,HumanActivityTracking> {
	String PEER_FIELD = "peer";
	String ALLOW_IN_EVALUATION_CONTEXT = "inScope:";
	String PAGE = "page";
	String CATALOG = "HumanActivityTracking";

	String getExitActivity();
	
	//TODO THERES MANY COMMIN FIELDS WITH ACTION REQUEST

	public FilterData getFilterData();

}
