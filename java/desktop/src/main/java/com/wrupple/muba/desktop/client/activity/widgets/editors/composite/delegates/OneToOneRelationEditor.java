package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.google.gwt.core.client.JavaScriptObject;
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

public class OneToOneRelationEditor extends AbstractValueRelationEditor<String> {

	public OneToOneRelationEditor(JavaScriptObject contextParameters, JavaScriptObject transactionDescriptor, ProcessContextServices contextServices,
			RelationshipDelegate delegate, FilterableDataProvider<JsCatalogEntry> dataProvider, HasData<JsCatalogEntry> dataWidget,
			JavaScriptObject formProperties, FieldDescriptor field, CatalogAction mode, int pageSize,boolean showAddition, boolean showRemoval) {
		super(contextParameters, contextServices, delegate, dataProvider, dataWidget, formProperties, field, mode, pageSize, showRemoval, showRemoval);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return super.getFilterValue();
	}

	@Override
	public void setValue(String value) {
		if(value==null){
			setFilterValue(null);
		}else{
			JsFilterData newfilter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, value);
			setFilterValue(newfilter);
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	protected void heyValueIsDiferentNow() {
		ValueChangeEvent.fire(this, getValue());
	}

}
