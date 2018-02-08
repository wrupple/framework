package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.SetActivity;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import com.wrupple.muba.event.domain.ApplicationState;

public class SetActivityImpl implements SetActivity{
    @Override
    public boolean execute(DesktopRequestContext context) throws Exception {

        ApplicationState newState = applicationStateProvider.get();
        newState.setHandleValue(initialState);

        return CONTINUE_PROCESSING;
    }
}
