package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasLocale;

public interface CatalogActionRequest extends HasLocale,CatalogChangeEvent{

	final String CATALOG = "CatalogAction";

	public final String FORMAT_PARAMETER = "format"; 

	public final String FILTER_DATA_PARAMETER = "filter";

	public final String PUBLISH_ACTION = "publish";

	public final String UPLOAD_ACTION = "upload";

	public final String UPLOAD_URL = "url";
	
	String getFormat();

	FilterData getFilter();

	boolean getFollowReferences();

	final String FULL_CACHE = "com.wrupple.fullcache";
	final String QUERY_CACHE = "com.wrupple.querycache";
	final String NO_CACHE = "com.wrupple.nocache";


    void setFollowReferences(boolean followReferences);


}
