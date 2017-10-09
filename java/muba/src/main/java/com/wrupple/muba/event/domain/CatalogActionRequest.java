package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasLocale;
import com.wrupple.muba.event.domain.reserved.HasParentValue;

public interface CatalogActionRequest extends HasLocale,DataEvent,HasParentValue<Void,CatalogActionRequest> {

	final String CATALOG = "CatalogAction";


	String getFormat();

	FilterData getFilter();

	boolean getFollowReferences();



    void setFollowReferences(boolean followReferences);


	void setFilter(FilterData filter);

	void setEntryValue(Object foreignValue);
}
