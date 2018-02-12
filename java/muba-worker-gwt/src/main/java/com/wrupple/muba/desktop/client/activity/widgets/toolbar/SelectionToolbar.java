package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleActivityToolbarBase;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.MultipleSelectionModel;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class SelectionToolbar extends WruppleActivityToolbarBase {

	
	
	private CatalogEntryBrowserMap browserMap;
	private SimpleLayoutPanel main;

	

	private ContentBrowser browser;


	@Inject
	public SelectionToolbar(CatalogEntryBrowserMap browserMap,ToolbarMap toolbarMap) {
		super(toolbarMap);
		this.browserMap = browserMap;
		this.main = new SimpleLayoutPanel();
		initWidget(main);
	}
	
	

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		super.initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);
		try{
			JavaScriptObject configuration = toolbarDescriptor.getPropertiesObject();
			/*
			 * Action toolbar is a content browser, so we get a configured instance like so
			 */
			GWTUtils.setAttribute(configuration, CatalogActionRequest.CATALOG_ID_PARAMETER, parameter.getCatalogId());
			browser = browserMap.getConfigured(configuration, contextServices, bus, contextParameters);
			/*
			 * attach
			 */
			main.setWidget(browser);
			HasData<JsCatalogEntry> ui = (HasData) contextServices.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();
			final MultipleSelectionModel selectionModel = (MultipleSelectionModel) ui.getSelectionModel();
			selectionModel.addSelectionChangeHandler(new Handler() {
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					JsArray<JsCatalogEntry> selectedItems = selectionModel.getSelectedItems();
					setValue(selectedItems);
				}
			});
		}catch(ClassCastException e){
			GWT.log("Selection toolbar in a not-selection transaction",e);
		}
		
	}
	
	@Override
	public void setValue(JavaScriptObject value) {
		if(value!=null){
			JsArray<JsCatalogEntry> v=value.cast();
				browser.setValue(v);
		}
	}

	@Override
	public JavaScriptObject getValue() {
		return browser.getValue();
	}
	

}
