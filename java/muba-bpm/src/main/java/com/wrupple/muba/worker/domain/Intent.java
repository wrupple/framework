package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.ManagedObject;

public interface Intent extends ManagedObject, DataContract {
    String Intent_CATALOG = "Intent";

    ApplicationState getStateValue();

    void setStateValue(ApplicationState applicationState);

    Object getState();
}
