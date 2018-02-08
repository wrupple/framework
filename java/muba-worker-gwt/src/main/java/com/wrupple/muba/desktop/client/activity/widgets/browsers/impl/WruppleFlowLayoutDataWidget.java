package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.worker.shared.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.worker.shared.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;

import javax.inject.Provider;
import java.util.HashSet;
import java.util.Set;

public class WruppleFlowLayoutDataWidget extends AbstractContentBrowser implements ContentBrowser {

	public static final String INLINE_PROP_VALUE = "inline";
	FlowDataPanel<JsCatalogEntry> browser;
	DelegateCell delegateCell;
	private CatalogFieldMap fieldFactory;

	@Inject
	public WruppleFlowLayoutDataWidget(CatalogFieldMap fieldFactory, Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProvider, ContentStyleDelegate styleDelegate, CatalogEntryBrowserMap browserMap) {
		super(new FlowDataPanel<JsCatalogEntry>(new DelegateCell(), keyProvider, styleDelegate), dinamicDataProviderProvider, keyProvider, styleDelegate,
				browserMap);
		this.fieldFactory = fieldFactory;
		browser = (FlowDataPanel) super.hasData;
		delegateCell = (DelegateCell) browser.getCell();
	}

	public void setCell(CatalogFormFieldProvider cellProvider, JavaScriptObject properties, EventBus bus) {
		Cell cell = cellProvider.createCell(bus, contextServices, contextParameters, properties, null, CatalogAction.READ);
		delegateCell.setCell(cell);
	}

	public void setCellWrapperClass(String cellWrapperClass) {
		browser.setCellWrapperClass(cellWrapperClass);
	}

	public void setInline(String layoutDelegate) {
		boolean inline = layoutDelegate == null || INLINE_PROP_VALUE.equals(layoutDelegate) || Boolean.parseBoolean(layoutDelegate);
		browser.setInline(inline);

	}

	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		browser.upateValue(visibleIndex, receivedUpdate);
	}

	public static class DelegateCell implements Cell<JsCatalogEntry> {

		Cell<JsCatalogEntry> cell;

		public Cell<JsCatalogEntry> getCell() {
			return cell;
		}

		public void setCell(Cell<JsCatalogEntry> cell) {
			this.cell = cell;
		}

		@Override
		public boolean dependsOnSelection() {
			return cell.dependsOnSelection();
		}

		@Override
		public Set<String> getConsumedEvents() {
			if (cell == null) {
				Set<String> regreso = new HashSet<String>(1);
				regreso.add("click");
				return regreso;
			}
			return cell.getConsumedEvents();
		}

		@Override
		public boolean handlesSelection() {
			return cell.handlesSelection();
		}

		@Override
		public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, JsCatalogEntry value) {
			return cell.isEditing(context, parent, value);
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, JsCatalogEntry value, NativeEvent event,
				ValueUpdater<JsCatalogEntry> valueUpdater) {
			cell.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, JsCatalogEntry value, SafeHtmlBuilder sb) {
			cell.render(context, value, sb);
		}

		@Override
		public boolean resetFocus(com.google.gwt.cell.client.Cell.Context context, Element parent, JsCatalogEntry value) {
			return cell.resetFocus(context, parent, value);
		}

		@Override
		public void setValue(com.google.gwt.cell.client.Cell.Context context, Element parent, JsCatalogEntry value) {
			cell.setValue(context, parent, value);
		}

	}

	@Override
    public void applyAlterations(ReconfigurationBroadcastEvent properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
        super.applyAlterations(properties, contextServices, eventBus, contextParamenters);
		// TODO what alteretaion can be applied?
	}
}
