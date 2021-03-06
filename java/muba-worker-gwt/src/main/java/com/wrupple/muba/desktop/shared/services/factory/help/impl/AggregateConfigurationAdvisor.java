package com.wrupple.muba.desktop.shared.services.factory.help.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.shared.services.factory.help.SolverConcensor;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;

public class AggregateConfigurationAdvisor implements SolverConcensor {

    final SolverConcensor[] advisor;

    public AggregateConfigurationAdvisor(SolverConcensor... advice) {
        this.advisor=advice;
	}

	@Override
	public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice) {
        for (SolverConcensor a : advisor) {
            a.conferWithRunners(currentState, advice);
        }
	}

	@Override
	public void validateValue(String fieldId, Object value, JsArrayString violations) {
		if(advisor.length>1){
			for(int i = 0; i< advisor.length; i++){
                advisor[i].intersectConstraintsWithSolution(fieldId, value, violations);
            }
		}
	}

	@Override
	public void setRuntimeParameters(String type, ProcessContextServices ctx) {
        for (SolverConcensor a : advisor) {
            a.setRuntimeParameters(type, ctx);
		}
	}

}
