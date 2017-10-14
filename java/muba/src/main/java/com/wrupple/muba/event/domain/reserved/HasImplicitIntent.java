package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.Event;

/**
 * Created by japi on 14/10/17.
 */
public interface HasImplicitIntent {

    public Event getImplicitIntentValue();

    public void setImplicitIntentValue(Event stateValue);


    public Object getImplicitIntent();

    public void setImplicitIntent(Object state);
}
