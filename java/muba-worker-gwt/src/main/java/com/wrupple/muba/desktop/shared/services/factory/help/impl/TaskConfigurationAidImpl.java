package com.wrupple.muba.desktop.shared.services.factory.help.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ServiceMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.shared.services.factory.help.SolverConcensor;
import com.wrupple.muba.desktop.shared.services.factory.help.TaskConfigurationAid;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;

public class TaskConfigurationAidImpl implements TaskConfigurationAid {

	private CatalogEditor<JsCatalogEntry> editor;
	private final CatalogEditorMap editorMap;
	private final CatalogEntryBrowserMap browserMap;
	private final TransactionPanelMap panelMap;
	private final ServiceMap services;
	ValueDependableConfigurationAdvisor comandAdvisor;
	AggregateConfigurationAdvisor browserAdvisor;
	AggregateConfigurationAdvisor editorAdvisor;
	private ProcessContextServices ctx;

	@Inject
	public TaskConfigurationAidImpl(ServiceMap servicesMap, CatalogEditorMap editorMap, CatalogEntryBrowserMap browserMap, TransactionPanelMap panelMap) {
		super();
		this.editorMap = editorMap;
		this.browserMap = browserMap;
		this.services = servicesMap;
		this.panelMap = panelMap;
	}

	@Override
	public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice) {
        SolverConcensor rootAdvisor = getRootAdvisor();
        rootAdvisor.conferWithRunners(currentState, advice);
    }

    private SolverConcensor getRootAdvisor() {
        // TODO add value change handlers and only change root advisor when user changes transaction type?
		String transactionType = (String) editor.getFieldValue(JsProcessTaskDescriptor.TRANSACTION_FIELD);
        SolverConcensor rootAdvisor;
        if (transactionType == null) {
			if (comandAdvisor == null) {
				comandAdvisor = new ValueDependableConfigurationAdvisor(services, ProcessTaskDescriptor.COMMAND_FIELD);
				comandAdvisor.setRuntimeParameters(null, ctx);
			}
			rootAdvisor = comandAdvisor;
		} else {
			if (CatalogSelectionActivity.BROWSE_COMMAND.equals(transactionType)) {
				if (browserAdvisor == null) {
					browserAdvisor = new AggregateConfigurationAdvisor(browserMap, panelMap);
				}
				rootAdvisor = browserAdvisor;
			} else {
				if (editorAdvisor == null) {
					editorAdvisor = new AggregateConfigurationAdvisor(editorMap, panelMap);
				}
				rootAdvisor = editorAdvisor;
			}
		}
		return rootAdvisor;
	}

	@Override
	public void validateValue(String fieldId, Object value, JsArrayString violations) {
        SolverConcensor rootAdvisor = getRootAdvisor();
        rootAdvisor.intersectConstraintsWithSolution(fieldId, value, violations);
    }

	@Override
	public void setRuntimeParameters(String type, ProcessContextServices ctx) {
		this.ctx=ctx;
		editor = (CatalogEditor) ctx.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();
	}

}
