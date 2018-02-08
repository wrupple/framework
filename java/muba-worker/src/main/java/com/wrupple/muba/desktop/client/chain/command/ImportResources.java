package com.wrupple.muba.desktop.client.chain.command;

import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.Command;

public interface ImportResources extends Command<ContextSwitchRuntimeContext> {

    interface Callback extends StateTransition<ContextSwitchRuntimeContext> {

    }
}
