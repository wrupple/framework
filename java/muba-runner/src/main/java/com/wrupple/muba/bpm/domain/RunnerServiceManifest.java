package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.reserved.HasCommand;

/**
 * Created by japi on 10/05/17.
 */
public interface RunnerServiceManifest extends ServiceManifest {
    final String SERVICE_NAME = HasCommand.COMMAND_FIELD;

}
