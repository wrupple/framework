package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasSentence;

public interface WorkerRequest extends Event, HasSentence{
    String CATALOG = "WorkerRequest";
}
