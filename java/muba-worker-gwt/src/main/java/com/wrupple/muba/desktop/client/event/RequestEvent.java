package com.wrupple.muba.desktop.client.event;

public interface RequestEvent {
    boolean isRequestHandled();

    void setRequestHandled(boolean requestExecuted);
}
