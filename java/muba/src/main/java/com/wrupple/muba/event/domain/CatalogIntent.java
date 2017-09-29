package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogKey;

/**
 * name = intent type
 * @author japi
 *
 */
public interface CatalogIntent extends Intent,HasCatalogKey {


    public final String DELETE_ACTION = "delete";

    public final String WRITE_ACTION = "write";

    public final String READ_ACTION = "read";

    public final String CREATE_ACTION = "new";


}
