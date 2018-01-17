package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.Event;

public interface WorkerLoadOrder extends Event {

    String CATALOG = "WorkerLoadOrder";


    //void setName(String desktopTitle);

    void setCharacterEncoding(String characterEncoding);

    String getHomeActivity();

    void setHomeActivity(String rootActivity);

    void setApplicationTree(Application domainRoot);

    void setSetupFlag(boolean configuracionRequired);
}
