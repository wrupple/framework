package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogKey;

/**
 * name = intent type
 * @author japi
 *
 */
public interface DataEvent extends Event,HasCatalogKey {


    public final String DELETE_ACTION = "delete";

    public final String WRITE_ACTION = "write";

    public final String READ_ACTION = "read";

    public final String CREATE_ACTION = "new";

    public final String FORMAT_PARAMETER = "format";

    public final String UPLOAD_ACTION = "upload";

    public final String UPLOAD_URL = "url";

    final String FULL_CACHE = "com.wrupple.fullcache";
    final String QUERY_CACHE = "com.wrupple.querycache";
    final String NO_CACHE = "com.wrupple.nocache";


}
