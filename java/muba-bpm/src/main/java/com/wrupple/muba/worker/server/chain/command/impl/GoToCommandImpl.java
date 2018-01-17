package com.wrupple.muba.worker.server.chain.command.impl;


import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.domain.Workflow;
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
        Workflow firstValue;
        if(output instanceof ServiceManifest){
            firstValue = (Workflow) output;
        }else{
            firstValue  = (Workflow) state.getUserSelectionValues().get(0);
        }

        state.setHandleValue(firstValue);
		return CONTINUE_PROCESSING;
	}


}
