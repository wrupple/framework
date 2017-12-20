package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.services.presentation.ToolbarAssemblyDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.vegetate.client.services.StorageManager;

import java.util.List;

public class ToolbarAssemblyDelegateImpl implements ToolbarAssemblyDelegate {

	private static final String TOOLBAR_SIZE_POSTFIX = "_toolbarSize";
	ToolbarMap toolbarRegistry;

	@Inject
	public ToolbarAssemblyDelegateImpl(ToolbarMap toolbarRegistry) {
		super();
		this.toolbarRegistry = toolbarRegistry;
	}

	@Override
	public void assebleToolbars(final ContentPanel transactionPanel, final TakesValue<?> mainTrasaction, final JsProcessTaskDescriptor parameter,
                                final JavaScriptObject taskDescriptorProps, final JsTransactionApplicationContext activityContext, final EventBus bus, final ProcessContextServices context,
                                final JsApplicationItem currentPlace) {

		JsArray<JsTaskToolbarDescriptor> toolbars = parameter.getToolbarsValuesArray();

		if (toolbars == null) {
			JsArrayString toolbarIds = parameter.getToolbarsArray();
			if (toolbarIds != null && toolbarIds.length() > 0) {
				StorageManager sm = context.getStorageManager();

				StateTransition<List<JsTaskToolbarDescriptor>> callback = new DataCallback<List<JsTaskToolbarDescriptor>>() {
					@Override
					public void execute() {
						buildTOolbars(taskDescriptorProps, JsArrayList.unwrap(result), transactionPanel, mainTrasaction, parameter, bus, context,
								activityContext, currentPlace);
					}
				};

				JsFilterData filter = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, toolbarIds);

				sm.read(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), TaskToolbarDescriptor.CATALOG, filter, callback);
			} else {
				JsArray<JsTaskToolbarDescriptor> preloaded = parameter.getToolbarsValuesArray();
				if (preloaded != null) {
					buildTOolbars(taskDescriptorProps, toolbars, transactionPanel, mainTrasaction, parameter, bus, context, activityContext, currentPlace);
				}
			}
		} else {
			buildTOolbars(taskDescriptorProps, toolbars, transactionPanel, mainTrasaction, parameter, bus, context, activityContext, currentPlace);
		}

	}

	private void buildTOolbars(JavaScriptObject taskDescriptorProps, JsArray<JsTaskToolbarDescriptor> toolbars, ContentPanel transactionView,
                               TakesValue<?> mainTrasaction, JsProcessTaskDescriptor parameter, EventBus bus, ProcessContextServices context,
                               JsTransactionApplicationContext contextParamenters, JsApplicationItem currentPlace) {

		JsTaskToolbarDescriptor toolbarDescriptor;
		if (toolbars != null) {
			for (int i = 0; i < toolbars.length(); i++) {
				toolbarDescriptor = toolbars.get(i);
				processToolbar(toolbarDescriptor, taskDescriptorProps, transactionView, parameter, contextParamenters, bus, context, mainTrasaction,
						currentPlace, i == (toolbars.length() - 1));
			}
		}
	}

	private void processToolbar(JsTaskToolbarDescriptor toolbarDescriptor, JavaScriptObject taskDescriptorProps, ContentPanel transactionView,
                                JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParamenters, EventBus bus, ProcessContextServices context,
                                TakesValue<?> mainTrasaction, JsApplicationItem currentPlace, boolean redraw) {
		String type = toolbarDescriptor.getType();
		String delegateTask = toolbarDescriptor.getTaskAsString();
		JavaScriptObject properties = toolbarDescriptor.getPropertiesObject();
		String toolbarId = toolbarDescriptor.getId();
		String name = toolbarDescriptor.getName();
		overwriteProperties(properties, delegateTask, type, toolbarId, name);

		Toolbar toolbar = toolbarRegistry.getConfigured(properties, context, bus, contextParamenters);
		// the reasoning behind notifying the ond of the configuration, and not
		// just waiting until a value is set is that toolbars need to have low
		// latency user responses, so preloading before they are used is
		// desirable.
		toolbar.initialize(toolbarDescriptor, parameter, contextParamenters, bus, context);

		// parseFloat("10.00")
		setSize(properties, taskDescriptorProps, TOOLBAR_SIZE_POSTFIX, name, type, toolbarId);
		GWTUtils.setAttribute(properties, ContentPanel.REDRAW_FLAG, redraw);
		transactionView.addToolbar(toolbar, properties);
	}

	private native void setSize(JavaScriptObject dest, JavaScriptObject sourc, String postfix, String name, String type, String id) /*-{
		if (dest.size == null) {
			var rawSize = null;
			if (id != null) {
				rawSize = sourc[id + postfix];
				if (rawSize != null) {
					dest.size = parseFloat(rawSize);
					return;
				}

			}

			if (type != null) {
				rawSize = sourc[type + postfix];
				if (rawSize != null) {
					dest.size = parseFloat(rawSize);
					return;
				}
			}

			if (name != null && name.indexOf(' ') >= 0) {
				rawSize = sourc[name + postfix];
				if (rawSize != null) {
					dest.size = parseFloat(rawSize);
				}
			}
		} else {
			dest.size = parseFloat(dest.size);
		}
	}-*/;

	private native void overwriteProperties(JavaScriptObject properties, String delegateTask, String type, String toolbarId, String name) /*-{
																																			properties.delegateTask = delegateTask;
																																			properties.type = type;
																																			properties.toolbarId = toolbarId;
																																			properties.name = name;
																																			}-*/;

}
