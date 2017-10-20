package com.wrupple.muba.bpm.server.chain.command.impl;


import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.server.chain.command.GoToCommand;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;

@Singleton
public class GoToCommandImpl implements GoToCommand {


	public GoToCommandImpl() {
	}

	@Override
	public boolean execute(Context ctx) {
		ApplicationContext context = (ApplicationContext) ctx;
		ApplicationState state= (ApplicationState) context.getStateValue();

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
