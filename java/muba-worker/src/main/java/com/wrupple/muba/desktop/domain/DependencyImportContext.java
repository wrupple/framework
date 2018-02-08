package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.ApplicationDependency;
import com.wrupple.muba.event.domain.reserved.HasDiscrimniator;
import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.Context;

public interface DependencyImportContext extends Context ,HasDiscrimniator {

    ContextSwitchRuntimeContext getApplicationSwitchContext();

    ApplicationDependency getDependency();

    StateTransition<DependencyImportContext> getCallback();


}
