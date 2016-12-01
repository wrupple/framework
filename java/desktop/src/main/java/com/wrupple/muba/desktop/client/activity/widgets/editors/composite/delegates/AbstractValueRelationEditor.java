package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.panels.BackAndForthPager;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

public abstract class AbstractValueRelationEditor<T> extends Composite implements HasValue<T>  {
	
	public interface RelationshipDelegate {

		void setValueChanger(StateTransition<JsFilterData> callback);
		
		void onRelationshipRemovalRequested(JsFilterData currentValues, String valueToRemove);

		void onRelationshipAdditionRequested(JsFilterData currentValue, JavaScriptObject contextParameters,
				ProcessContextServices contextServices);

		void changeValue(JsArrayString ids);
	}

	private final RelationshipDelegate delegate;
	private final FilterableDataProvider<JsCatalogEntry> dataProvider;
	private final CaptionPanel wrapper;
	private final HasData<JsCatalogEntry> dataWidget;
	private final int visibleItems;
	
	private final JavaScriptObject contextParameters;
	private final ProcessContextServices contextServices;

	private JsFilterData filterValue;

	public AbstractValueRelationEditor(JavaScriptObject contextParameters, ProcessContextServices contextServices, final RelationshipDelegate delegate,
			FilterableDataProvider<JsCatalogEntry> dataProvider, final HasData<JsCatalogEntry> dataWidget, JavaScriptObject formProperties,
			FieldDescriptor field, CatalogAction mode, int pageSize, boolean showAddition,boolean showRemoval) {
		super();
		this.contextParameters = contextParameters;
		this.contextServices = contextServices;
		this.dataWidget = dataWidget;
		

		ComplexPanel structure = new VerticalPanel();

		
		
		delegate.setValueChanger(new ChangeValue());
		
		if (CatalogAction.READ != mode) {
			FlowPanel topPanel = new FlowPanel();
			if (mode != CatalogAction.READ) {
				if(showAddition){
					topPanel.add(new Button("+", new AdditionClickHandler()));
				}
				
				if(showRemoval){
					topPanel.add( new Button("-",new RemoveClickHandler(field)));
				}
				
			}
			structure.add(topPanel);
		}
		if (pageSize > 0) {
			// TODO parametrize pager type
			BackAndForthPager pager = new BackAndForthPager(0, pageSize, 1);
			dataWidget.setVisibleRange(0, pageSize);
			pager.setDisplay(dataWidget);
			structure.add(pager);
			visibleItems = pageSize;
		} else {
			dataWidget.setVisibleRange(0, Integer.MAX_VALUE);
			structure.add(((IsWidget) dataWidget).asWidget());
			visibleItems = Integer.MAX_VALUE;
		}
		if (mode == CatalogAction.READ) {
			wrapper = null;
			initWidget(structure);
		} else {
			wrapper = new CaptionPanel(dataProvider.getCatalog());
			wrapper.add(structure);
			initWidget(wrapper);
		}

		this.delegate = delegate;
		this.dataProvider = dataProvider;
	}

	protected JsArrayString getFilterValues(){
		if(filterValue==null){
			return null;
		}
		return this.filterValue.fetchCriteria(CatalogEntry.ID_FIELD).getValuesArray().cast();
	}
	
	protected String getFilterValue(){
		if(getFilterValues()==null||getFilterValues().length()==0){
			return null;
		}
		return getFilterValues().get(0);
	}
	
	private class AdditionClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			delegate.onRelationshipAdditionRequested(filterValue, contextParameters, contextServices);
		}

	}
	
	private class RemoveClickHandler implements ClickHandler {

		private FieldDescriptor field;

		public RemoveClickHandler(FieldDescriptor field) {
			this.field=field;
		}

		@Override
		public void onClick(ClickEvent event) {
			
			MultiSelectionModel<JsCatalogEntry> model = (MultiSelectionModel) dataWidget.getSelectionModel();
			Set<JsCatalogEntry> selected=model.getSelectedSet();
			if(selected!=null){
				for(JsCatalogEntry e : selected){
					delegate.onRelationshipRemovalRequested(filterValue, e.getId());
					model.setSelected(e, false);
				}
			}
			
			
		}

	}

	private class ChangeValue extends DataCallback<JsFilterData> {

		@Override
		public void execute() {
			setFilterValue(result);
			heyValueIsDiferentNow();
		}

	}


	protected void setFilterValue(JsFilterData value) {
		if(value!=null){
			JsFilterCriteria criteria = value.fetchCriteria(CatalogEntry.ID_FIELD);
			if(criteria==null){
				value=null;
			}else{
				if(criteria.getValuesArrayOrNull()==null || criteria.getValuesArrayOrNull().length()==0){
					value=null;
				}
			}
		}
		this.filterValue = value;
		Range range;
		if (value == null) {
			range = new Range(0, 0);
			dataWidget.setVisibleRange(range);
		} else {
			dataProvider.setFilter(value);
			dataWidget.setVisibleRangeAndClearData(new Range(0, this.visibleItems), true);
			try {
				this.dataProvider.addDataDisplay(this.dataWidget);
			} catch (Exception e) {

			}
		}
		
	}


	protected abstract void heyValueIsDiferentNow() ;

	
}
