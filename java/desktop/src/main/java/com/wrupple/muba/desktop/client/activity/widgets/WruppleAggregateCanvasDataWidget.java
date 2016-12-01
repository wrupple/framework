package com.wrupple.muba.desktop.client.activity.widgets;

import java.util.List;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AbstractContentBrowser;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateDataCanvas;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateDataCanvas.AggregateRenderService;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class WruppleAggregateCanvasDataWidget extends AbstractContentBrowser
		implements ContentBrowser {


	@Inject
	public WruppleAggregateCanvasDataWidget(
			Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProvider, CatalogEntryBrowserMap browserMap) {
		super(new AggregateDataCanvas<JsCatalogEntry>(null, 0, 0), dinamicDataProviderProvider, keyProvider, null, browserMap);
		super.pagingEnabled=false;
	}
	
	@Override
	public void setRuntimeParams(String catalog, JavaScriptObject properties,
			EventBus bus, JsTransactionActivityContext contextParameters,
			ProcessContextServices contextServices) {
		setCumulative("true");
		super.setRuntimeParams(catalog, properties, bus, contextParameters, contextServices);
	}
	
	
	/**
	 * @param cell the cell to set
	 */
	public void setRenderService(AggregateRenderService<JsCatalogEntry> cell) {
		AggregateDataCanvas<JsCatalogEntry> canvas = (AggregateDataCanvas<JsCatalogEntry>) super.hasData;
		canvas.setCell(cell);
	}
	
	@Override
	protected void onLoad() {
		setCanvasSize();
		super.onLoad();
	}
	

	@Override
	public void onResize() {
		 setCanvasSize() ;
		super.onResize();
	}
	
	@Override
	public void setRowData(int start, List<? extends JsCatalogEntry> values) {
		super.setRowData(start, values);
		setCanvasSize();
	}
	
	private void setCanvasSize() {
		AggregateDataCanvas<JsCatalogEntry> canvas = (AggregateDataCanvas<JsCatalogEntry>) super.hasData;
		
		int height= GWTUtils.getNonZeroParentHeight(this);
		int width = GWTUtils.getNonZeroParentWidth(this);
		canvas.setPaperHeight(height);
		canvas.setPaperWidth(width);
		
	}


	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		//TODO
	}


	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionActivityContext contextParamenters) {
		super.applyAlterations(properties, contextServices, eventBus, contextParamenters);
		// TODO what alteretaion can be applied?
	}
	
	public String getGroupingField() {
		AggregateDataCanvas<JsCatalogEntry> canvas = (AggregateDataCanvas<JsCatalogEntry>) super.hasData;
		return canvas.getGroupingField();
	}

	public void setGroupingField(String groupingField) {
		AggregateDataCanvas<JsCatalogEntry> canvas = (AggregateDataCanvas<JsCatalogEntry>) super.hasData;

		canvas.setGroupingField(groupingField);
	}
}
