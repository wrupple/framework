package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class OneToManyRelationEditor extends AbstractValueRelationEditor<JsArrayString> {

	
	
	public OneToManyRelationEditor(JavaScriptObject contextParameters, ProcessContextServices contextServices, RelationshipDelegate delegate,
			FilterableDataProvider<JsCatalogEntry> dataProvider, HasData<JsCatalogEntry> dataWidget, JavaScriptObject formProperties,
			FieldDescriptor field, CatalogAction mode, int pageSize, boolean showAddition,boolean showRemoval) {
		super(contextParameters, contextServices, delegate, dataProvider, dataWidget, formProperties, field, mode, pageSize, showAddition, showRemoval);
	}

	@Override
	public JsArrayString getValue() {
		return super.getFilterValues();
	}
	
	@Override
	public void setValue(JsArrayString value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<JsArrayString> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(JsArrayString value) {
		if(value==null||value.length()==0){
			setFilterValue(null);
		}else{
			JsFilterData newfilter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, value);
			setFilterValue(newfilter);
		}
	}
	

	@Override
	protected void heyValueIsDiferentNow() {
		ValueChangeEvent.fire(this, getValue());
	}
}
