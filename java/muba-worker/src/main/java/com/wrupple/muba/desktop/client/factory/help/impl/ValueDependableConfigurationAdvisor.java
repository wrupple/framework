package com.wrupple.muba.desktop.client.factory.help.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.help.SolverConcensor;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public class ValueDependableConfigurationAdvisor implements SolverConcensor {

    final SolverConcensor valueMap;
    final String field;
	private CatalogEditor<JsCatalogKey> editor;

    public ValueDependableConfigurationAdvisor(SolverConcensor valueMap, String field) {
        super();
		this.valueMap = valueMap;
		this.field = field;
	}

	@Override
	public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice) {
		String discriminator = (String) editor.getFieldValue(field);
		
		if (discriminator != null) {
			GWTUtils.setAttribute(currentState, field, discriminator);
            valueMap.conferWithRunners(currentState, advice);
        }
	}

	@Override
	public void validateValue(String fieldId, Object value, JsArrayString violations) {
		// FIXME is dependable value ? (on another field)
        valueMap.intersectConstraintsWithSolution(fieldId, value, violations);
    }

	@Override
	public void setRuntimeParameters(String type, ProcessContextServices ctx) {
		editor = (CatalogEditor) ctx.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();
	}


}
