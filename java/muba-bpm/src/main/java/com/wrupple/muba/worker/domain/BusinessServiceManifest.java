package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ParentServiceManifest;

/**
 * Created by japi on 29/07/17.
 */
public interface BusinessServiceManifest extends ParentServiceManifest {
    String SERVICE_NAME = "activity";

    String SOLVE = "solve";

}
