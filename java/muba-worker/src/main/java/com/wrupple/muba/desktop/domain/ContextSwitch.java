package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.Event;

public interface ContextSwitch extends Event {

    String CATALOG = "ContextSwitch";

    ContainerState getOrderValue();

    void setOrderValue(ContainerState request);
}
