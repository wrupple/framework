package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.event.EntriesDeletedEvent;
import com.wrupple.muba.desktop.client.event.EntriesRetrivedEvent;
import com.wrupple.muba.desktop.client.event.EntryCreatedEvent;
import com.wrupple.muba.desktop.client.event.EntryUpdatedEvent;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.ThemedImagesDictionary;
import com.wrupple.muba.desktop.client.services.command.InterruptActivity;
import com.wrupple.muba.desktop.client.services.logic.ConfigurationConstants;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;

/**
 * Default size is 60 px or 9%
 * 
 * @author japi
 * 
 */
public class ActionToolbar extends ResizeComposite implements Toolbar {

	class ActionsLoadedCallback extends DataCallback<List<JsWruppleActivityAction>> {

		final JsTransactionApplicationContext ctx;
		final ProcessContextServices services;
		final EventBus bus;
		final JsProcessTaskDescriptor toolbarDescriptor;

		public ActionsLoadedCallback(ProcessContextServices services, JsTransactionApplicationContext ctx, JsProcessTaskDescriptor toolbarDescriptor, EventBus bus) {
			super();
			this.services = services;
			this.bus = bus;
			this.ctx = ctx;
			this.toolbarDescriptor = toolbarDescriptor;
		}

		@Override
		public void execute() {
			
			JsArray<JsWruppleActivityAction> actionsAndChildren = JavaScriptObject.createArray().cast();
			

			if(renderPlaceChildren){
				collectCurrentPlaceChildre(actionsAndChildren);
			}
			
			if (result != null && !result.isEmpty()) {
				for (JsWruppleActivityAction act : result) {
					processThemeImages(act);
					actionsAndChildren.push(act);
				}
			}
			addActions(actionsAndChildren, bus);
		}

	}

	private ProcessContextServices context;
	private JsTransactionApplicationContext contextParameters;
	private JsArray<JsCatalogEntry> actions;
	private CatalogEntryBrowserMap browserMap;
	private SimpleLayoutPanel main;
	private ConfigurationConstants contsnts;
	private ThemedImagesDictionary themeDictionary;

	

	private ContentBrowser browser;
	private boolean renderPlaceChildren=false;
	private String cell;

	@Inject
	public ActionToolbar(CatalogEntryBrowserMap browserMap, ConfigurationConstants contsnts,ThemedImagesDictionary themeDictionary) {
		super();
		this.browserMap = browserMap;
		this.actions = JavaScriptObject.createArray().cast();
		this.contsnts = contsnts;
		this.themeDictionary=themeDictionary;
		this.main = new SimpleLayoutPanel();
		initWidget(main);
	}
	

	public void processThemeImages(JsWruppleActivityAction act) {
		if(act.getImage()==null){
			JavaScriptObject p = act.getPropertiesObject();
			String themeImage = GWTUtils.getAttribute(p, ImageTemplate.THEMED_RESOURCE);
			if(themeImage!=null){
				SafeUri uri = themeDictionary.get(themeImage).getSafeUri();
				act.setStaticImageUri(uri);
			}
		}
	}


	protected void collectCurrentPlaceChildre(JsArray<JsWruppleActivityAction> actionsAndChildren) {
		DesktopManager dm = context.getDesktopManager();
		JavaScriptObject jsoItem = dm.getCurrentApplicationItem();

		if (jsoItem != null) {

			JsApplicationItem item = jsoItem.cast();
			JsArray<JsApplicationItem> children = item.getChildItemsValuesArray();

			if (children != null) {

				JsWruppleActivityAction newAction;
				JsApplicationItem child;
				for (int i = 0; i < children.length(); i++) {
					child = children.get(i);
					newAction = transformChildCurrentPlaceChildIntoAction(child);
					actionsAndChildren.push(newAction);
				}

			}

		}

	}

	private JsWruppleActivityAction transformChildCurrentPlaceChildIntoAction(JsApplicationItem child) {
		String activity = child.getActivity();
		String name = child.getName();
		String image = child.getImage();
		JsWruppleActivityAction action = JsCatalogEntry.createCatalogEntry(WruppleActivityAction.CATALOG).cast();
		action.setCommand(InterruptActivity.COMMAND + " " + activity);
		action.setName(name);
		action.setImage(image);
		return action;
	}

	public void addActions(JsArray<JsWruppleActivityAction> newActions, final EventBus bus) {
		if(newActions==null){
			return;
		}
		if (browser == null) {
			int width = newActions.length()*48;
			if(width<=0){
				width=48;
			}
			/*
			 * Action toolbar is a content browser, so we get a configured instance like so
			 */
			JavaScriptObject configuration = contsnts.getIconBrowser("48", "column,"+width,WruppleActivityAction.CATALOG,cell);
			browser = browserMap.getConfigured(configuration, context, bus, contextParameters);
			/*
			 * attach
			 */
			browser.asWidget().getElement().getStyle().setProperty( "overflow", "hidden");
			main.setWidget(browser);
			
			/*
			 * Fire Action when user selects it
			 */
			final SingleSelectionModel<JsCatalogEntry> selectionModel = new SingleSelectionModel<JsCatalogEntry>();
			selectionModel.addSelectionChangeHandler(new Handler() {

				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					final JsCatalogEntry selected = selectionModel.getSelectedObject();
					if (selected != null) {
						final JsWruppleActivityAction action = selected.cast();
						JavaScriptObject actionProperties = action.getPropertiesObject();
						String actionCommand = action.getCommand();
						StateTransition<JsTransactionApplicationContext> unblockcallback = new DataCallback<JsTransactionApplicationContext>() {
							@Override
							public void execute() {
								selectionModel.setSelected(selected, false);
							}
						};
						context.getServiceBus().excecuteCommand(actionCommand, actionProperties, bus, context, contextParameters, unblockcallback);
					}
				}
			});

			browser.setSelectionModel(selectionModel);
			
		}
		JsWruppleActivityAction value;
		JsCatalogEntry a;
		for( int i  = 0 ; i< newActions.length() ; i++){
			value = newActions.get(i);
			a=value.cast();
			actions.push(a);
		}
		browser.setValue(actions);

		// grid.setOverflow("hidden");
	}

	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {

	}

	@Override
	public void setValue(JavaScriptObject value) {
		JsArray<JsCatalogEntry> v = value.cast();
		browser.setValue(v);
	}

	@Override
	public JavaScriptObject getValue() {
		return browser.getValue().cast();
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		this.context = contextServices;
		this.contextParameters = contextParameters;
		ActionsLoadedCallback callback = new ActionsLoadedCallback(contextServices, contextParameters, parameter, bus);
		JsArray<JsWruppleActivityAction> userActionValues = parameter.getUserActionValues();

		if (userActionValues == null) {
			StorageManager sm = context.getStorageManager();
			JsArrayString userActions = parameter.getUserActionsArray();
			if (userActions == null || userActions.length() == 0) {
				callback.setResultAndFinish(null);
			} else {
				JsFilterData filter = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, userActions);
				sm.read(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(),  WruppleActivityAction.CATALOG, filter, callback);
			}
		} else {
			List<JsWruppleActivityAction> result = JsArrayList.arrayAsList(userActionValues);
			callback.setResultAndFinish(result);
		}
	}

	@Override
	public void setType(String s) {

	}
	
	public void setCell(String cell){
		this.cell=cell;
	}
	
	public void setRenderPlaceChildren(String bool){
		this.renderPlaceChildren = Boolean.parseBoolean(bool);
	}


	@Override
	public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
		
	}


	@Override
	public void onEntriesRetrived(EntriesRetrivedEvent e) {
		
	}


	@Override
	public void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent) {
		
	}


	@Override
	public void onEntryCreated(EntryCreatedEvent entryCreatedEvent) {
		
	}


	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JavaScriptObject> handler) {
		return null;
	}

}