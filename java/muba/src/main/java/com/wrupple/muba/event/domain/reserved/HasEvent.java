package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.Event;

/**
 * Created by japi on 14/10/17.
 */
public interface HasEvent {

    public Event getEventValue();

    public void setEventValue(Event stateValue);


    public Object getEvent();

    public void setEvent(Object state);
}
