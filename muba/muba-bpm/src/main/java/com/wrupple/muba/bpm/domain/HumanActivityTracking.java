package com.wrupple.muba.bpm.domain;

import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.structure.HasParentValue;

public interface HumanActivityTracking extends HasParentValue<String,HumanActivityTracking> {
	String PEER_FIELD = "peer";
	String ALLOW_IN_EVALUATION_CONTEXT = "inScope:";
	String PAGE = "page";

	String getExitActivity();
	
	//TODO THERES MANY COMMIN FIELDS WITH ACTION REQUEST

	public FilterData getFilterData();

}
