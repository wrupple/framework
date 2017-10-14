package com.wrupple.muba.event.domain.reserved;

import org.apache.commons.chain.Context;

public interface HasLiveContext {


    Object getState();

    Context getStateValue();

    void setStateValue(Context context);
}
