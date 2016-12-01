package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class MapCellProvider implements CatalogFormFieldProvider {

	@Override
	public Cell<? extends Object> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionActivityContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		assert d.getDefaultValueOptions() != null : "Atemmpting to initialize a List Picker as a field with no default options";
		
		
		if (CatalogAction.READ == mode) {
			
			List<String> rawOptions = d.getDefaultValueOptions();
			Map<String,String> valueMap = new HashMap<String,String>();
			String displayValue;
			String systemValue;
			String[] split;
			
			for(String rawOption : rawOptions){
				split = rawOption.split("=");
				if(split.length>1){
					systemValue=split[0];
					displayValue = split[1];
					valueMap.put(systemValue, displayValue);
				}
			}
			
			
			return new MapTextCell(valueMap);
		} else {
			List<String> rawOptions = d.getDefaultValueOptions();
			List<String> displayValues = new ArrayList<String>(rawOptions.size()+1);
			displayValues.add("...");
			Map<String,String> valueMap = new HashMap<String,String>();
			String displayValue;
			String systemValue;
			String[] split;
			
			for(String rawOption : rawOptions){
				split = rawOption.split("=");
				if(split.length>1){
					systemValue=split[0];
					displayValue = split[1];
					displayValues.add(displayValue);
					valueMap.put(displayValue, systemValue);
				}
			}
			
			Cell<String> wrapped = new MapSelectionCell(displayValues,valueMap);
			return wrapped;
		}
	}
	
	public static class MapTextCell  extends TextCell {

		
		private Map<String, String> valueMap;

		public MapTextCell(Map<String,String> valueMap){
			super();
			this.valueMap=valueMap;
		}
		
		  @Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String data, SafeHtmlBuilder sb) {
			if(data!=null){
				data = valueMap.get(data);
			}
			  
			super.render(context, data, sb);
		}

		
	}
	

	public static class MapSelectionCell extends SelectionCell {

		private List<String> options;
		private Map<String, String> valueMap;

		public MapSelectionCell(List<String> options,
				Map<String, String> valueMap) {
			super(options);
			this.options=options;
			this.valueMap=valueMap;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			String type = event.getType();
			if (BrowserEvents.CHANGE.equals(type)) {
				Object key = context.getKey();
				SelectElement select = parent.getFirstChild().cast();
				String newValue = options.get(select.getSelectedIndex());
				newValue = valueMap.get(newValue);
				setViewData(key, newValue);
				finishEditing(parent, newValue, key, valueUpdater);
				if (valueUpdater != null) {
					valueUpdater.update(newValue);
				}
			}
			super.onBrowserEvent(context, parent, value, event, null);
		}
		
		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String value, SafeHtmlBuilder sb) {
			if(value!=null){
				Set<Entry<String, String>> entries = valueMap.entrySet();
				//incomming system value
				String systemValue;
				String displayValue;
				for(Entry<String, String> entry: entries){
					displayValue = entry.getKey();
					systemValue = entry.getValue();
					if(value.equals(systemValue)){
						value= displayValue;
					}
				}
				//outgoing display value
			}
			
			super.render(context, value, sb);
		}

	}

}
