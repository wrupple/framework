package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasRunner;

public interface ContainerState extends Event, HasRunner {

    String CATALOG = "ContainerState";


    public ApplicationState getStateValue();
    //void setName(String desktopTitle);

    void setCharacterEncoding(String characterEncoding);

    String getHomeActivity();

    void setHomeActivity(String rootActivity);

    public Application getHomeApplicationValue();

    void setApplicationTree(Application domainRoot);

    void setRunner(Long greeterRunner);
}
