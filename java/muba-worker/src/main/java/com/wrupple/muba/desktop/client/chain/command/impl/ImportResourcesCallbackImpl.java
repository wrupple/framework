package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.server.chain.command.impl.ParallelProcess;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.impl.Callback;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ImportResourcesCallbackImpl extends Callback<ContextSwitchRuntimeContext> implements ImportResourcesCallback {

    @Inject
    public ImportResourcesCallbackImpl(HandleContainerState fire) {
        super(fire);
    }


}
