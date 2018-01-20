package com.wrupple.muba.desktop.client.service;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;

public interface SliceReader {
    Application getInitialActivity(ContainerState request, RuntimeContext parent);
}
