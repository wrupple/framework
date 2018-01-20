package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.worker.domain.ApplicationState;

public interface ContextSwitch extends Event {

    ContainerState getOrderValue();

    ApplicationState getState();

    Application getHomeApplicationValue();


}
