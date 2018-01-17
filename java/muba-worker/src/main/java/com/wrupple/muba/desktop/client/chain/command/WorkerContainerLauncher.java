package com.wrupple.muba.desktop.client.chain.command;

import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;

public interface WorkerContainerLauncher extends RequestInterpret {
     ContainerContext getContainer();
}
