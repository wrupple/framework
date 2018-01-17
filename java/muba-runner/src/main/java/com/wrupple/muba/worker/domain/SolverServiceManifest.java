package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.reserved.HasCommand;

/**
 * Created by japi on 10/05/17.
 */
public interface SolverServiceManifest extends ServiceManifest {
    String SERVICE_NAME = HasCommand.COMMAND_FIELD;

}
