package com.wrupple.muba.event.domain;

public interface Intent extends ManagedObject, DataContract {
    String Intent_CATALOG = "Intent";

    ApplicationState getStateValue();

    void setStateValue(ApplicationState applicationState);

    Object getState();
}
