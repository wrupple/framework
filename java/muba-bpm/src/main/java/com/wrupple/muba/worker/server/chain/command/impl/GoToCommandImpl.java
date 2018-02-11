package com.wrupple.muba.worker.server.chain.command.impl;


import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.GoToCommand;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;

@Singleton
public class GoToCommandImpl implements GoToCommand {


	public GoToCommandImpl() {
	}

	@Override
	public boolean execute(Context ctx) {
		ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();

		//Workflow currentItem = state.getApplicationValue();
        CatalogEntry output = state.getEntryValue();
        Application firstValue;
        if(output instanceof ServiceManifest){
            firstValue = (Application) output;
        }else{
            firstValue  = (Application) state.getUserSelectionValues().get(0);
        }

        state.setApplicationValue(firstValue);
		return CONTINUE_PROCESSING;
	}


}
