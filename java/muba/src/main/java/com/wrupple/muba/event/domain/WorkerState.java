package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasRunner;
import com.wrupple.muba.event.domain.reserved.HasSentence;

import java.util.List;

public interface WorkerState extends Event, HasRunner, HasSentence {

    String CATALOG = "WorkerState";

    /**
     *
     * @return the index at which the application's sentence iterator is at
     */
    Long getWordIndex();

    ApplicationState getStateValue();
    //void setName(String desktopTitle);

    void setCharacterEncoding(String characterEncoding);

    String getHomeActivity();

    void setHomeActivity(String rootActivity);

    Application getHomeApplicationValue();

    void setApplicationTree(Application domainRoot);
    Application getApplicationTree();

    void setRunner(Long defaultRunner);

    void setSentence(List<String> sentence);

    void setWordIndex(Long i);

    void setStateValue(ApplicationState applicationState);
}
