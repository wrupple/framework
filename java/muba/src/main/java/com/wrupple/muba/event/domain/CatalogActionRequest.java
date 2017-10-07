package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasLocale;

public interface CatalogActionRequest extends HasLocale,DataEvent {

	final String CATALOG = "CatalogAction";


	String getFormat();

	FilterData getFilter();

	boolean getFollowReferences();



    void setFollowReferences(boolean followReferences);


	void setFilter(FilterData filter);

	void setEntryValue(Object foreignValue);
}
