package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogKey;

/**
 * name = intent type
 * @author japi
 *
 */
public interface DataEvent extends Event,HasCatalogKey {


    String DELETE_ACTION = "delete";

    String WRITE_ACTION = "write";

    String READ_ACTION = "read";

    String CREATE_ACTION = "new";

    String FORMAT_PARAMETER = "format";


    String FULL_CACHE = "com.wrupple.fullcache";
    String QUERY_CACHE = "com.wrupple.querycache";
    String NO_CACHE = "com.wrupple.nocache";


}
