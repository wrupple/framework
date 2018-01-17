package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.worker.domain.Application;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.domain.WorkerLoadOrder;

public interface ContextSwitch extends Event {

    WorkerLoadOrder getOrderValue();

    ApplicationState getState();

    Application getHomeApplicationValue();


}
