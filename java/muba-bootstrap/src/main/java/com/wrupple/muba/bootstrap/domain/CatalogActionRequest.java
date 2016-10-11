package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;

public interface CatalogActionRequest extends CatalogEntry,HasLocale,HasEntryId,HasCatalogId{

	final String CATALOG = "CatalogAction";

	public final String CATALOG_ACTION_PARAMETER = "action";

	public final String FORMAT_PARAMETER = "format"; 

	public final String FILTER_DATA_PARAMETER = "filter";


	/*
	 * ACTIONS
	 */
	public final String DELETE_ACTION = "delete";

	public final String WRITE_ACTION = "write";

	public final String READ_ACTION = "read";

	public final String CREATE_ACTION = "new";

	public final String PUBLISH_ACTION = "publish";

	public final String LIST_ACTION_TOKEN = "list";

	public final String UPLOAD_ACTION = "upload";

	public final String UPLOAD_URL = "url";

	String getAction();
	
	String getFormat();

	FilterData getFilter();

	Object getEntryValue();

	final String FULL_CACHE = "com.wrupple.fullcache";
	final String QUERY_CACHE = "com.wrupple.querycache";
	final String NO_CACHE = "com.wrupple.nocache";
	

}
