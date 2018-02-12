package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogTypeSelectionTask;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.ConfigurationConstants;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsArrayList;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.CatalogDescriptor;

import java.util.Collections;
import java.util.List;

public class CatalogTypeSelectionTaskImpl extends ResizeComposite implements CatalogTypeSelectionTask {

	private SimpleLayoutPanel main;
	private ConfigurationConstants constanst;
	private CatalogEntryBrowserMap browserMap;
	private ProcessContextServices context;
	private DesktopTheme theme;
	
	@Inject
	public CatalogTypeSelectionTaskImpl(ConfigurationConstants constanst, CatalogEntryBrowserMap browserMap, DesktopTheme theme){
		main = new SimpleLayoutPanel();
		initWidget(main);
		this.browserMap=browserMap;
		this.theme=theme;
		this.constanst=constanst;
	}
	
	@Override
	public void start(List<JsCatalogEntry> p, final StateTransition<List<DesktopPlace	>> onDone, EventBus bus) {
		JsArray<JsCatalogEntry> parameter = JsArrayList.unwrap(p).cast();
		SafeUri defaulti = theme.catalog().getSafeUri();
		JsCatalogKey key;
		for(JsCatalogEntry c : p ){
			if(c.getImage()==null){
				key = c.cast();
				key.setImage("data:image");
				key.setStaticImageUri(defaulti);
			}
		}
		JavaScriptObject configuration = constanst.getIconBrowser("175", String.valueOf(175*5),CatalogDescriptor.CATALOG_ID,null);
		JsTransactionApplicationContext contextParameters= JsTransactionApplicationContext.createObject().cast();
		ContentBrowser browser = browserMap.getConfigured(configuration, context, bus, contextParameters);
		final SingleSelectionModel<JsCatalogEntry> selectionModel= new SingleSelectionModel<JsCatalogEntry>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if(selectionModel.getSelectedObject()!=null){
					JsCatalogEntry key=selectionModel.getSelectedObject();
					JsCatalogEntry catalogid =key.cast();

					DesktopPlace result = new DesktopPlace(CatalogSelectionActivity.ACTIVITY_ID);
					result.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, catalogid.getId());
					result.setImage(catalogid.getImage());
					result.setName(catalogid.getName());
					onDone.setResultAndFinish(Collections.singletonList(result));
				}
				
			}
		});
		browser.setSelectionModel(selectionModel);
		main.setWidget(browser);
		browser.setValue(parameter);
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context=context;
	}

	
}
