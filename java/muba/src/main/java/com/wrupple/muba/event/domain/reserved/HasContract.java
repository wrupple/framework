package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.Contract;

/**
 * Created by japi on 14/10/17.
 */
public interface HasContract {

    public Contract getEventValue();

    public void setEventValue(Contract stateValue);


    public Object getEvent();

    public void setEvent(Object state);
}
