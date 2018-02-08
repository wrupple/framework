package com.wrupple.muba.cms.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.CatalogEntryCreateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryImportActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryUpdateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.worker.shared.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.worker.shared.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.command.InterruptActivity;
import com.wrupple.muba.desktop.client.services.logic.ProcessSwitchCommand;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;

import javax.inject.Provider;

public class SimpleContentManager<V extends JavaScriptObject> implements ContentManager<V> {

	protected  String managedCatalog;

	protected CatalogEntryBrowserMap catalogEntryBrowserMap;
	protected CatalogEditorMap editorMap;
	protected Provider<TransactionalActivityAssembly> processAssemblyProvider;
	protected DesktopTheme theme;

	public SimpleContentManager(String managedCatalog, DesktopTheme theme, CatalogEntryBrowserMap catalogEntryBrowserMap, CatalogEditorMap editorMap,
			Provider<TransactionalActivityAssembly> processAssemblyProvider) {
		super();
		this.theme = theme;
		this.managedCatalog = managedCatalog;
		this.catalogEntryBrowserMap = catalogEntryBrowserMap;
		this.editorMap = editorMap;
		this.processAssemblyProvider = processAssemblyProvider;
	}




	@Override
	public HumanTaskProcessor<V,V> getCreateTransaction(JsTransactionApplicationContext contextParameters, JavaScriptObject properties, EventBus bus,
                                                        ProcessContextServices contextServices) {
		return assembleEditorWidget(getCatalog(), properties, bus, contextServices, CatalogAction.CREATE, contextParameters);
	}

	@Override
	public HumanTaskProcessor<V,V> getReadTransaction(JsTransactionApplicationContext contextParameters, JavaScriptObject properties, EventBus bus,
                                                      ProcessContextServices contextServices) {
		return assembleEditorWidget(getCatalog(), properties, bus, contextServices, CatalogAction.READ, contextParameters);
	}

	@Override
	public HumanTaskProcessor<V,V> getUpdateTransaction(JsTransactionApplicationContext contextParameters, JavaScriptObject properties, EventBus bus,
                                                        ProcessContextServices contextServices) {
		return assembleEditorWidget(getCatalog(), properties, bus, contextServices, CatalogAction.UPDATE, contextParameters);
	}

	public CatalogEditor assembleEditorWidget(String catalog, JavaScriptObject properties, EventBus bus, ProcessContextServices services, CatalogAction mode,
			JsTransactionApplicationContext processContextParams) {

		CatalogEditor<JsCatalogEntry> editor = editorMap.getConfigured(properties, services, bus, processContextParams);

		editor.initialize(catalog, mode, bus, services, properties, processContextParams);
		bus.addHandler(EntryUpdatedEvent.getType(), editor);
		bus.addHandler(EntriesDeletedEvent.getType(), editor);
		bus.addHandler(EntriesRetrivedEvent.getType(), editor);

		return editor;
	}

	@Override
	public HumanTaskProcessor<JsArray<V>,JsFilterData> getSelectTransaction(JsTransactionApplicationContext ctx, JavaScriptObject properties, EventBus bus,
                                                                            ProcessContextServices services) {

		final ContentBrowser widget = catalogEntryBrowserMap.getConfigured(properties, services, bus, ctx);

		bus.addHandler(EntryCreatedEvent.getType(), widget);
		bus.addHandler(EntryUpdatedEvent.getType(), widget);
		bus.addHandler(EntriesDeletedEvent.getType(), widget);
		bus.addHandler(EntriesRetrivedEvent.getType(), widget);

		return (HumanTaskProcessor) widget;
	}

	@Override
	public com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext, JsTransactionApplicationContext> getEditingProcess(CatalogAction mode,
                                                                                                                                            EventBus bus, ProcessContextServices contextServices) {
		com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext, JsTransactionApplicationContext> regreso = new SequentialProcess<JsTransactionApplicationContext, JsTransactionApplicationContext>();
		TransactionalActivityAssembly processAssembly = processAssemblyProvider.get();
		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();
		JsWruppleActivityAction tempAction = getCommitAction(theme.ok(), "Commit");
		actions.push(tempAction);
		// TODO ? does the process need an application item?
		ApplicationItem applicationItem = null;
		processAssembly.setApplicationItem(applicationItem);
		String transaction;

		if (mode == CatalogAction.CREATE) {
			transaction = CatalogActionRequest.CREATE_ACTION;
		} else if (mode == CatalogAction.READ) {
			transaction = CatalogActionRequest.READ_ACTION;
		} else {
			transaction = CatalogActionRequest.WRITE_ACTION;
		}

		JsArray<JsProcessTaskDescriptor> processSteps = generateCatalogTransactionDescriptor(transaction, managedCatalog, false, actions, null, null);

		processAssembly.assembleNativeProcess(regreso, processSteps);
		return regreso;
	}

	@Override
	public Process<JsTransactionApplicationContext, JsTransactionApplicationContext> getSelectionProcess(ProcessContextServices contextServices, boolean multiple,
                                                                                                         boolean creationAction) {
		com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext, JsTransactionApplicationContext> regreso = new SequentialProcess<JsTransactionApplicationContext, JsTransactionApplicationContext>();
		TransactionalActivityAssembly processAssembly = processAssemblyProvider.get();

		JsArray<JsProcessTaskDescriptor> processSteps = defaultzSelectionProcess(multiple, creationAction, theme, managedCatalog);
		// TODO ? does the process need an application item?
		ApplicationItem applicationItem = null;
		processAssembly.setApplicationItem(applicationItem);

		processAssembly.assembleNativeProcess(regreso, processSteps);

		return regreso;
	}

	public static JsArray<JsProcessTaskDescriptor> defaultzSelectionProcess(boolean multiple, boolean creationAction, DesktopTheme theme, String managedCatalog) {
		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();

		if (creationAction) {
			JsWruppleActivityAction jcreationAction = JavaScriptObject.createObject().cast();

			jcreationAction.setCommand(ProcessSwitchCommand.COMMAND);
			jcreationAction.setName("Create");
			jcreationAction.setImageResource(theme.create());
			jcreationAction.addProperty(ProcessSwitchCommand.ID_PARAMETER, CatalogActionRequest.CREATE_ACTION + ":" + managedCatalog);
			actions.push(jcreationAction);
		}
		if (multiple) {
			actions.push(getCommitAction(theme.ok(), "Commit"));
		}

		String transaction = CatalogSelectionActivity.BROWSE_COMMAND;
		// TODO Auto-generated method stub
		JsArray<JsProcessTaskDescriptor> processSteps = generateCatalogTransactionDescriptor(transaction, managedCatalog, multiple, actions, null, null);
		return processSteps;
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultSelectionProcess(DesktopTheme theme) {
		return getDefaultSelectionProcess(theme, null, false);
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultSelectionProcess(DesktopTheme theme, String catalog, boolean multiple) {
		return getDefaultSelectionProcess(theme, catalog, multiple, null, null);
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultSelectionProcess(DesktopTheme theme, String catalog, boolean multiple, String widget,
			String task.getProducedField()) {

		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();
		if (multiple) {
			actions.push(SimpleContentManager.getCommitAction(theme.ok(), "Commit"));
		}
		JsWruppleActivityAction tempAction = SimpleContentManager.getCommitAction(theme.create(), "Create");
		tempAction.setCommand(InterruptActivity.COMMAND);
		tempAction.addProperty("targetActivity", CatalogEntryCreateActivity.ACTIVITY_ID);
		actions.push(tempAction);

		tempAction = SimpleContentManager.getCommitAction(theme.imnport(), "Import");

		tempAction.setCommand(InterruptActivity.COMMAND);
		tempAction.addProperty("targetActivity",GWTUtils.asTokenizedAddress( CatalogEntryImportActivity.ACTIVITY_ID));
		actions.push(tempAction);

		JsArray<JsProcessTaskDescriptor> regreso = generateCatalogTransactionDescriptor(CatalogSelectionActivity.BROWSE_COMMAND, catalog, multiple, actions,
				task.getProducedField(), widget);
		regreso.get(0).addProperty(ContentBrowser.COMMIT_ON_SELECT, "true");
		return regreso;
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultReadProcess(DesktopTheme theme) {
		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();
		JsWruppleActivityAction tempAction = SimpleContentManager.getAction(theme.ok(), "Commit", CommitCommand.COMMAND);
		actions.push(tempAction);
		tempAction = SimpleContentManager.goToActivityAction(theme.edit(), "Edit");
		tempAction.addProperty("targetActivity", CatalogEntryUpdateActivity.ACTIVITY_ID);
		actions.push(tempAction);
		tempAction = SimpleContentManager.getAction(theme.delete(), "Commit", CommitCommand.COMMAND);
		tempAction.addProperty(CommitCommand.CANCEL_CONTEXT_PROPERTY, "true");
		/*
		 * tempAction =
		 * SimpleContentManager.getSingleDeleteAction(theme.delete(), "Delete",
		 * CatalogConstants.CATALOG_ID_PARAMETER,
		 * CatalogConstants.DELETE_ACTION);
		 */

		actions.push(tempAction);
		JsArray<JsProcessTaskDescriptor> regreso = SimpleContentManager.generateCatalogTransactionDescriptor(CatalogActionRequest.READ_ACTION, null, false,
				actions, null, null);
		return regreso;
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultUpdateProcess(DesktopTheme theme, String catalog) {
		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();
		JsWruppleActivityAction tempAction = SimpleContentManager.getCommitAction(theme.ok(), "Commit");
		actions.push(tempAction);
		JsArray<JsProcessTaskDescriptor> regreso = SimpleContentManager.generateCatalogTransactionDescriptor(CatalogActionRequest.WRITE_ACTION, catalog, false,
				actions, null, null);

		JsArray<JsProcessTaskDescriptor> readProcess = generateCatalogTransactionDescriptor(CatalogActionRequest.READ_ACTION, catalog, false, actions, null, null);

		JsProcessTaskDescriptor value;
		for (int i = 0; i < readProcess.length(); i++) {
			value = readProcess.get(i);
			regreso.push(value);
		}
		return regreso;
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultUpdateProcess(DesktopTheme theme) {

		return getDefaultUpdateProcess(theme, null);
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultCreationProcess(DesktopTheme theme) {
		return getDefaultCreationProcess(theme, null, null);
	}

	public static JsArray<JsProcessTaskDescriptor> getDefaultCreationProcess(DesktopTheme theme, String catalog, String task.getProducedField()) {
		JsArray<JsWruppleActivityAction> actions = JavaScriptObject.createArray().cast();
		JsWruppleActivityAction tempAction = SimpleContentManager.getCommitAction(theme.ok(), "Commit");
		actions.push(tempAction);
		JsArray<JsProcessTaskDescriptor> process = SimpleContentManager.generateCatalogTransactionDescriptor(CatalogActionRequest.CREATE_ACTION, catalog, false,
				actions, task.getProducedField(), null);

		JsArray<JsProcessTaskDescriptor> readProcess = generateCatalogTransactionDescriptor(CatalogActionRequest.READ_ACTION, null, false, actions, null, null);

		JsProcessTaskDescriptor value;
		for (int i = 0; i < readProcess.length(); i++) {
			value = readProcess.get(i);
			process.push(value);
		}

		return process;
	}

	public static native JsArray<JsProcessTaskDescriptor> getNavigateSelectionProcess() /*-{
		return [ {
			"transactionType" : "navigate",
			"properties" : [],
			CatalogEntry.NAME_FIELD : "Navigate",
			"catalog" : "TaskDescriptor",
			"id" : "0",
			"userActions" : [],
			"catalogId" : "DesktopPlace",
			"machineTaskCommandName" : null,
			"toolbars" : []
		} ];
	}-*/;

	public static native JsArray<JsProcessTaskDescriptor> generateCatalogTransactionDescriptor(String transaction, String catalog, boolean multiple,
			JsArray<JsWruppleActivityAction> actions, String task.getProducedField(), String widget) /*-{
        var task;
		if (actions == null || actions.length == 0) {
			task = {
				transactionType : transaction,
				catalogId : catalog
			};
		} else {
			task = {
				transactionType : transaction,
				catalogId : catalog,
				toolbarsValues : [ {
					type : "action"
				} ],
				userActionsValues : actions
			};
		}
		if (multiple) {
			task.properties = ["selectionModel="
							+ @com.wrupple.muba.desktop.client.services.impl.MultipleSelectionModel::NAME,
					@com.wrupple.muba.desktop.client.services.SelectionModelDictionary::SELECTION_HANDLER
							+ "="
							+ @com.wrupple.muba.desktop.client.services.SelectionModelDictionary::NO_SELECTION_HANDLER ];
		}
		if (widget != null) {
			var widgetProperty = "widget=" + widget;
			if (task.properties == null) {
				task.properties = [];
			}
			task.properties.push(widgetProperty);
		}

		if (task.getProducedField() != null) {
			if (task.properties == null) {
				task.properties = [];
			}
			task.properties.push("task.getProducedField()=" + task.getProducedField());
		}

		return [ task ];
	}-*/


    public static JsWruppleActivityAction getCommitAction(ImageResource resource, String actionName) {

		return getAction(resource, actionName, CommitCommand.COMMAND);
	}

	private static JsWruppleActivityAction getAction(ImageResource resource, String actionName, String command) {
		JsWruppleActivityAction r = JsWruppleActivityAction.createObject().cast();
		r.setCommand(command);
		r.setImageResource(resource);
		r.setName(actionName);
		return r;
	}

	public static JsWruppleActivityAction goToActivityAction(ImageResource resource, String actionName) {

		return getAction(resource, actionName, InterruptActivity.COMMAND);
	}

	public static JsWruppleActivityAction invokeCommandWithActionProperty(ImageResource resource, String actionName, String command, String actionProperty) {
		JsWruppleActivityAction r = getAction(resource, actionName, command);
		r.addProperty("action", actionProperty);
		return r;
	}




	@Override
	public String getCatalog() {
		return managedCatalog;
	}


}
