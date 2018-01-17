package com.wrupple.muba.worker.shared.domain;


public interface PanelTransformationConfig {


    String getToolbarId();

    String getTarget();

    String getAlterationCommand();

    boolean getFireReset();

    void setType(String type);

    void setWidget(String widget);
}