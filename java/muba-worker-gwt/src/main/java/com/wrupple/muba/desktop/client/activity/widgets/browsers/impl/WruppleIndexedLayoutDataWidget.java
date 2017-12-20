package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.LayoutDataPanel.DataWidgetFactory;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;
import com.wrupple.muba.desktop.client.services.presentation.ModifyUserInteractionStatePanelCommand;
import com.wrupple.muba.desktop.client.services.presentation.layout.CellPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.List;

public class WruppleIndexedLayoutDataWidget extends AbstractContentBrowser
		implements ContentBrowser {

	public static class DelegatingDataWidgetFactory implements
			DataWidgetFactory<JsCatalogEntry> {

		private Cell<JsCatalogEntry> cell;
		private HashMap<String, HasValue<JsCatalogEntry>> map;
		private String cellClass;

		@Inject
		public DelegatingDataWidgetFactory() {
			super();
			map = new HashMap<String, HasValue<JsCatalogEntry>>();
		}

		public DelegatingDataWidgetFactory(Cell<JsCatalogEntry> cell,
				String cellClass) {
			this();
			this.cell = cell;
			this.cellClass = cellClass;
		}

		@Override
		public IsWidget getWidget(JsCatalogEntry element) {
			assert cell != null : "cell has not been initialized";
			String id = element.getId();
			// cause an html editor to be added?
			CellWidget<JsCatalogEntry> regreso = (CellWidget<JsCatalogEntry>) map
					.get(id);
			if (regreso == null) {
				regreso = new CellWidget<JsCatalogEntry>(cell, element);
				map.put(id, regreso);
			} else {
				regreso.setValue(element, true, true);
			}
			if (cellClass != null) {
				
				regreso.addStyleName(cellClass);
			}
			return regreso;
		}

		public void setCell(Cell<JsCatalogEntry> cell) {
			this.cell = cell;
		}

		public String getCellClass() {
			return cellClass;
		}

		@Override
		public void setCellClass(String cellClass) {
			this.cellClass = cellClass;
		}

		public Cell<JsCatalogEntry> getCell() {
			return cell;
		}

		@Override
		public void updatevalue(int index, JsCatalogEntry value) {
			String id = value.getId();
			HasValue<JsCatalogEntry> w = map.get(id);
			if (w != null) {
				w.setValue(value);
			}
		}

	}

	class NotifyToolbarsScrollHandler implements ScrollHandler {

		@Override
		public void onScroll(ScrollEvent event) {
			applyAlterationsOnToolbars();
		}

	}

	private DelegatingDataWidgetFactory delegatingFactory;
	private String[] declaredRulerIds;

	private CatalogFieldMap fieldFactory;
	private double ZOOM_DELTA = .1;
	private String transition;

	@Inject
	public WruppleIndexedLayoutDataWidget(
			CatalogFieldMap fieldFactory,
			IndexedLayoutDelegate indexedLayoutDelegate,
			DelegatingDataWidgetFactory delegatingFactory,
			Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProvider,
			ContentStyleDelegate styleDelegate, CatalogEntryBrowserMap browserMap) {
		super(new LayoutDataPanel<JsCatalogEntry>(delegatingFactory,
				indexedLayoutDelegate, styleDelegate),
				dinamicDataProviderProvider, keyProvider, styleDelegate, browserMap);
		super.pagingEnabled = false;
		declaredRulerIds = null;
		this.fieldFactory = fieldFactory;
		this.delegatingFactory = delegatingFactory;
	}

	@Override
	public void setRowData(int start, List<? extends JsCatalogEntry> values) {
		super.setRowData(start, values);
		// NOTIFY RULERS OF BUCKET DIMENSIONS
		if (declaredRulerIds != null) {
			applyAlterationsOnToolbars();
		}
	}

	private void applyAlterationsOnToolbars() {
		if (isAttached()) {
			LayoutDataPanel<JsCatalogEntry> layoutDataPanel = (LayoutDataPanel<JsCatalogEntry>) super.hasData;
			IndexedLayoutDelegate layoutDelegate = layoutDataPanel
					.getIndexedLayoutDelegate();
			CellPositioner positioner = layoutDelegate.getCellPositioner();
			int currentRulerBucketSize = positioner
					.getRulerBucketSizeInPixels();
			double currentBucketValue = positioner.getRulerBucketValue();
			int viewportHeight = layoutDataPanel.getViewPortHeight();
			int viewportWidth = layoutDataPanel.getViewPortWidth();
			int verticalScroll = layoutDataPanel.getVerticalScroll();
			int horisontalScroll = layoutDataPanel.getHorisontalScroll();
			JavaScriptObject actionProperties;
			for (String rulerId : declaredRulerIds) {
				actionProperties = generateRulerProperties(verticalScroll,
						horisontalScroll, viewportWidth, viewportHeight,
						rulerId, currentBucketValue, currentRulerBucketSize);

				contextServices.getServiceBus().excecuteCommand(
						ModifyUserInteractionStatePanelCommand.COMMAND,
						actionProperties, eventBus, contextServices,
						contextParameters, null);
			}
		}
	}

	private native JavaScriptObject generateRulerProperties(int vs, int hs,
			int vpw, int vph, String rulerToolbarId, double currentBucketValue,
			int currentRulerBucketSize) /*-{
		return {
			verticalScroll : vs,
			horisontalScroll : hs,
			viewportWidth : vpw,
			viewportHeight : vph,
			panelAlterationTarget : "panel",
			panelAlterationToolbarId : rulerToolbarId,
			rulerBucketValue : currentBucketValue,
			rulerBucketSize : currentRulerBucketSize
		};
	}-*/;


	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		delegatingFactory.updatevalue(visibleIndex, receivedUpdate);
	}
	
	public void setZoomFactor(String zoomExpression){
		if(zoomExpression!=null){
			LayoutDataPanel<JsCatalogEntry> wrapped = (LayoutDataPanel<JsCatalogEntry>) super.hasData;
			if(wrapped!=null){
				double zoomFactor;
				try{
					zoomFactor =Double.parseDouble(zoomExpression);
					wrapped.setZoomFactor(zoomFactor);
				}catch(NumberFormatException e){
					if ("zoom-in".equals(zoomExpression)) {
						 zoomFactor = wrapped.getZoomFactor() + ZOOM_DELTA;
						 wrapped.setZoomFactor(zoomFactor);
					} else if ("zoom-out".equals(zoomExpression)) {
						 zoomFactor = wrapped.getZoomFactor() - ZOOM_DELTA;
						wrapped.setZoomFactor(zoomFactor);
					}
					
				}
			}
		}
	}
	
	
	public void setTransition(String transition){
		this.transition=transition;
	}
	

	public void setCell(CatalogFormFieldProvider cellProvider, JavaScriptObject properties,EventBus bus){
		Cell cell = cellProvider.createCell(bus, contextServices,
				contextParameters, properties, null, CatalogAction.READ);
		delegatingFactory.setCell(cell);
	}
	
	public void setRulerToolbars(String rulerToolbars){
        // TODO IS THIS REALLT NECESARRY, cant a suscription via event processSwitches be
        // enough?
		if (rulerToolbars != null) {
			String[] rulers = rulerToolbars.split(",");
			if (rulers.length > 0) {
				declaredRulerIds = rulers;
				LayoutDataPanel<JsCatalogEntry> layoutDataPanel = (LayoutDataPanel<JsCatalogEntry>) super.hasData;
				layoutDataPanel
						.addScrollHandler(new NotifyToolbarsScrollHandler());
			}
		}
	}

	public void setLayoutDelegate(
			IndexedLayoutDelegate indexedLayoutDelegate) {
		LayoutDataPanel<JsCatalogEntry> underlyingDataWidget = (LayoutDataPanel<JsCatalogEntry>) super.hasData;
		underlyingDataWidget.setIndexedLayoutDelegate(indexedLayoutDelegate);
	}


	/*
	 * This might actually be useful for other things private CellPosition
	 * multiplyArea( CellPosition p, int scalar) { double a =
	 * p.getHeight()*p.getWidth(); double length = Math.sqrt((a*scalar));
	 * 
	 * double delta = length - p.getWidth();
	 * 
	 * double x = p.getX()-(delta/2); double y = p.getY()-(delta/2); double
	 * width = length; double height = length; return new CellPosition(x, y,
	 * width, height); }
	 */
}