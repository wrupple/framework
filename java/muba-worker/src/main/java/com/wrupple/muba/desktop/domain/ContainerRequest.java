package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasSentence;

public interface ContainerRequest extends Event, HasSentence{
    String CATALOG = "ContainerRequest";
}
