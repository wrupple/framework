package com.wrupple.muba.worker.domain;

public interface ProcessInstance {

    Number getProcess();

    int getStepIndex();

    int getStatus();

    String getSerializedContext();
}
