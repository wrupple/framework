package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.ServiceManifest;

/**
 * Created by japi on 29/07/17.
 */
public interface BusinessServiceManifest extends ParentServiceManifest {
    final String  SERVICE_NAME = "activity";

    final String  SOLVE = "solve";

}
