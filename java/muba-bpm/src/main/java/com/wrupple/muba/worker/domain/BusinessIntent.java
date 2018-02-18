package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.ManagedObject;

public interface BusinessIntent extends ManagedObject, DataEvent {
    String BusinessIntent_CATALOG = "BusinessIntent";

    //Date getDue();

    ApplicationState getStateValue();

    void setStateValue(ApplicationState applicationState);

    Object getState();
}
