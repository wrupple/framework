package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.reserved.HasSentence;

public interface WorkerContract extends Contract, HasSentence{
    String CATALOG = "WorkerContract";

    //Date getDue();

    Long getRunner();

    String getRootActivity();
}
