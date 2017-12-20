package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;
public class ListCellProvider implements CatalogFormFieldProvider {

	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		assert d.getDefaultValueOptions() != null : "Atemmpting to initialize a List Picker as a field with no default options";
		if(CatalogAction.READ==mode){
			//FIXME all normalized selection fields have a number data type
			if(JsCatalogEntry.INTEGER_DATA_TYPE==d.getDataType()){
				return new NormalizedTextCell(d);
			}else{
				return new TextCell();
			}
			
		}else{
			ArrayList<String> list = new ArrayList<String>(d.getDefaultValueOptions().size()+1);
			list.add("...");
			list.addAll(d.getDefaultValueOptions());
			Cell<String> wrapped = new NormalizedSelectionCell(list,d);
			return wrapped;
		}
	}
	
	
	public static class NormalizedTextCell  extends TextCell {

		FieldDescriptor field;
		
		public NormalizedTextCell(FieldDescriptor field){
			super();
			this.field=field;
		}
		
		  @Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String data, SafeHtmlBuilder sb) {
			if(data!=null){
				int index = Integer.parseInt(data);
				
				List<String> fieldOptions = field.getDefaultValueOptions();
				if(index>=0&&index<fieldOptions.size()){
					data = fieldOptions.get(index);
				}else{
					data=null;
				}
			}
			  
			super.render(context, data, sb);
		}

		
	}
	
	public static class NormalizedSelectionCell extends SelectionCell {

		private List<String> options;
		private FieldDescriptor field;

		public NormalizedSelectionCell(List<String> options, FieldDescriptor d) {
			super(options);
			this.field=d;
			this.options=options;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			
			if(JsCatalogEntry.INTEGER_DATA_TYPE==field.getDataType()){
				String type = event.getType();
				if (BrowserEvents.CHANGE.equals(type)) {
					Object key = context.getKey();
					SelectElement select = parent.getFirstChild().cast();
					
					String newValue = options.get(select.getSelectedIndex());
					int newValueIndex=field.getDefaultValueOptions().indexOf(newValue);
					if(newValueIndex<0){
						newValueIndex=0;
					}
					String actualNewValue= String.valueOf(newValueIndex);
					
					setViewData(key, actualNewValue);
					finishEditing(parent, actualNewValue, key, valueUpdater);
					if (valueUpdater != null) {
						valueUpdater.update(actualNewValue);
					}
				}
				super.onBrowserEvent(context, parent, value, event, null);
			}else{
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
			}
			
		}
		
		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String value, SafeHtmlBuilder sb) {
			if(value!=null && !value.isEmpty()){
				//incomming system value is index of field options
				try{
					int index = Integer.parseInt(value);
					List<String> fieldOptions = field.getDefaultValueOptions();
					if(index>=0&&index<fieldOptions.size()){
						value = fieldOptions.get(index);
					}else{
						value=null;
					}
				}catch(NumberFormatException e){
					
				}
			}
			super.render(context, value, sb);
		}

	}

}
