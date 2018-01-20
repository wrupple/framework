package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleActivityToolbarBase;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class CMSToolbar extends WruppleActivityToolbarBase implements Toolbar {

	private final SimpleLayoutPanel panel;
	private final StorageManager sm;
	private TakesValue<? extends JavaScriptObject> transactionView;
	private String delegateTask;
	private String catalog;
	private ContentManagementSystem cms;

	@Inject
	public CMSToolbar( StorageManager sm, ContentManagementSystem cms, ToolbarMap toolbarMap) {
		super(toolbarMap);
		this.cms = cms;
		this.sm = sm;
		this.panel = new SimpleLayoutPanel();
		initWidget(panel);
	}

	public void initialize(final JsTaskToolbarDescriptor toolbarDescriptor, final JsProcessTaskDescriptor parameter,
                           final JsTransactionApplicationContext contextParameters, final EventBus bus, final ProcessContextServices contextServices) {
		super.initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);

		StateTransition<JsProcessTaskDescriptor> callback = new DataCallback<JsProcessTaskDescriptor>() {

			@Override
			public void execute() {
				if (result != null) {
					result.insertProperties(toolbarDescriptor.getPropertiesArray());
				} else {
					throw new IllegalArgumentException("Task Descriptor " + delegateTask + " is not present");
				}
				JavaScriptObject properties = result.getTaskPropertiesObject();
				String catalog = result.getCatalogId();
				ContentManager<JsCatalogEntry> cm = cms.getContentManager(catalog);
				String transaction = result.getTransactionType();
				JavaScriptObject initialValue;
                // TODO still some repeated logic setRuntimeContext UserInteractionState
                //TODO register transaction view setRuntimeContext event processSwitches
                if (CatalogActionRequest.READ_ACTION.equals(transaction)) {
					transactionView = cm.getReadTransaction(contextParameters, properties, bus, contextServices);
					panel.setWidget((IsWidget) transactionView);
				} else if (CatalogActionRequest.WRITE_ACTION.equals(transaction)) {
					transactionView = cm.getUpdateTransaction(contextParameters, properties, bus, contextServices);
					panel.setWidget((IsWidget) transactionView);
				} else {
					transactionView = cm.getSelectTransaction(contextParameters, properties, bus, contextServices);
					panel.setWidget((IsWidget) transactionView);
					JsApplicationItem applicationItem = contextServices.getDesktopManager().getCurrentApplicationItem().cast();

					JsArray<JsApplicationItem> childrenArray = null;
					if (applicationItem != null) {
						childrenArray = applicationItem.getChildItemsValuesArray();
					}

					if (catalog.equals(ApplicationItem.CATALOG) && result.getCurrentPlaceNavigationFlag()) {
						initialValue = childrenArray;
					} else {
						// TODO read filtervalue from configuration
						initialValue = JsFilterData.newFilterData();
					}
					if (!Boolean.parseBoolean(GWTUtils.getAttribute(properties, "disableBrowserInit"))) {
						setValue(initialValue);
					}
				}

			}
		};
		if (delegateTask == null) {
			JsProcessTaskDescriptor subtaskDescriptor = JavaScriptObject.createObject().cast();
			assert catalog != null;
			subtaskDescriptor.setCatalogId(catalog);
			//subtaskDescriptor.setPropertiesArray(toolbarDescriptor.getPropertiesArray());
			subtaskDescriptor.setTransactionType(type);
			callback.setResultAndFinish(subtaskDescriptor);
		} else {
			sm.read(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(),ProcessTaskDescriptor.CATALOG, delegateTask, callback);
		}

	}


	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}


	@Override
	public void setValue(JavaScriptObject value) {
		((TakesValue) transactionView).setValue(value);
	}

	@Override
	public JavaScriptObject getValue() {
        return transactionView.getValue();
    }

	public String getDelegateTask() {
		return delegateTask;
	}

	public void setDelegateTask(String delegateTask) {
		this.delegateTask = delegateTask;
	}


}
