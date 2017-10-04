package com.wrupple.muba.desktop.client.services.presentation.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.DataBox;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.AbstractForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.client.services.presentation.ForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

public class ForeignSelectBoxHandle extends AbstractForeignRelationWidgetHandle implements ForeignRelationWidgetHandle {

	final Provider<GenericDataProvider> dinamicDataProviderProvider;
	private RelationshipDelegate delegate;
	private FieldDescriptor field;

	@Inject
	public ForeignSelectBoxHandle(Provider<GenericDataProvider> dinamicDataProviderProvider) {
		this.dinamicDataProviderProvider = dinamicDataProviderProvider;
	}

	@Override
	public HasData<JsCatalogEntry> get(Cell<JsCatalogEntry> cell) {
		
		// this data widget receives all selected entres as rows, but has an
		// internal copy of all foreign entries available
		
		JsFilterData filter = JsFilterData.newFilterData();
		filter.setConstrained(true);
		filter.setStart(0);
		filter.setLength(Integer.MAX_VALUE);
		return new SelectBoxBrowser(field.getForeignCatalogName(), filter);
	}

	@Override
	public boolean showAddRelation() {
		return false;
	}

	@Override
	public boolean showRemoveSelectionFromRelation() {
		return false;
	}

	@Override
	public void init(FieldDescriptor field, JavaScriptObject fieldProperties, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices, RelationshipDelegate delegate, GenericDataProvider dataProvider, CatalogAction mode) {
		this.delegate = delegate;
		this.field=field;
	}

	class SelectBoxBrowser extends Composite implements HasData<JsCatalogEntry> {

		final DataBox main;
		private  ArrayList<JsCatalogEntry> visible;
		private Range range;
		public SelectBoxBrowser(String catalog, FilterData filter) {
			super();
			main = new DataBox();
			visible = new ArrayList<JsCatalogEntry>();
			range = new Range(0, 1);
			GenericDataProvider provider = dinamicDataProviderProvider.get();

			provider.setCatalog(catalog);
			provider.setUseCache(false);
			provider.setFilter(filter);
			provider.addDataDisplay(main);
			main.setVisibleRangeAndClearData(new Range(0,Integer.MAX_VALUE), true);

			main.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					JsCatalogEntry e = main.getSelectedEntry();
					JsArrayString arr = JsArrayString.createArray().cast();
					if (e != null) {
						arr.push(e.getId());
					}
					delegate.changeValue(arr);
				}
			});
			initWidget(main);
		}

		@Override
		public void setRowData(int start, List<? extends JsCatalogEntry> values) {
			for (int i = 0; i < values.size(); i++) {
				main.setSelection(values.get(i));
			}
		}

		@Override
		public HandlerRegistration addRangeChangeHandler(Handler handler) {
			return main.addRangeChangeHandler(handler);
		}

		@Override
		public HandlerRegistration addRowCountChangeHandler(com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
			return main.addRowCountChangeHandler(handler);
		}

		@Override
		public int getRowCount() {
			return visible.size();
		}

		@Override
		public Range getVisibleRange() {
			return range;
		}

		@Override
		public boolean isRowCountExact() {
			return true;
		}

		@Override
		public void setRowCount(int count) {
			if(count<getRowCount()){
				visible  = new ArrayList<JsCatalogEntry>(visible.subList(0, count-1));
				RowCountChangeEvent.fire(this, getRowCount(), true);
			}
		}

		@Override
		public void setRowCount(int count, boolean isExact) {
			setRowCount(count);
		}

		@Override
		public void setVisibleRange(int start, int length) {
			setVisibleRange(new Range(start,length));
		}

		@Override
		public void setVisibleRange(Range range) {
			this.range=range;
		}
		
		@Override
		public JsCatalogEntry getVisibleItem(int indexOnPage) {
			return visible.get(indexOnPage);
		}

		@Override
		public Iterable<JsCatalogEntry> getVisibleItems() {
			return visible;
		}

		@Override
		public void setVisibleRangeAndClearData(Range range, boolean forceRangeChangeEvent) {
			setVisibleRange(range);
			if(forceRangeChangeEvent){
				RangeChangeEvent.fire(this, range);
			}
		}

		@Override
		public HandlerRegistration addCellPreviewHandler(com.google.gwt.view.client.CellPreviewEvent.Handler<JsCatalogEntry> handler) {
			return addCellPreviewHandler(handler);
		}

		@Override
		public SelectionModel<? super JsCatalogEntry> getSelectionModel() {
			return main.getSelectionModel();
		}


		@Override
		public void setSelectionModel(SelectionModel<? super JsCatalogEntry> selectionModel) {
			main.setSelectionModel(selectionModel);
		}

		@Override
		public int getVisibleItemCount() {
			return visible.size();
		}

	}

}
