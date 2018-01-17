package com.wrupple.muba.desktop.client.activity.widgets.fields.column;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.util.List;
import java.util.Set;

/**
 * FIXME rewrite using all available metada on field descriptors
 * 
 * @author japi
 *
 */
public class ForeignEntryColumn extends FieldColumn {

	private JsArrayAdapterCell wrappingCell;
	private FieldDescriptor fieldDescriptor;

	public ForeignEntryColumn(JsArrayAdapterCell cell, FieldDescriptor field,
			FieldConversionStrategy cs, List<FilterCriteria> includeCriteria) {
		super(field.getFieldId(), cell, cs, includeCriteria);
		this.wrappingCell = cell;
		this.fieldDescriptor = field;
	}

	@Override
	public Object getValue(JsCatalogEntry object) {
		Object localFieldValue = super.getValue(object);
		if(fieldDescriptor.isEphemeral()){
			wrappingCell.setValuesAvailable(true);
			wrappingCell.setExpectPluralValues(true);
			return localFieldValue;
		}else{
			JavaScriptObject maybeFieldValue;
			String maybeFieldId = id + JsCatalogEntry.MULTIPLE_FOREIGN_KEY;
			// first asume plural relationship
			maybeFieldValue = GWTUtils.getAttributeAsJavaScriptObject(object,
					maybeFieldId);
			wrappingCell.setExpectPluralValues(true);
			if (maybeFieldValue == null) {
				// try singular relationship
				maybeFieldId = id + JsCatalogEntry.FOREIGN_KEY;
				maybeFieldValue = GWTUtils.getAttributeAsJavaScriptObject(object,
						maybeFieldId);
				wrappingCell.setExpectPluralValues(false);
			}
			if (maybeFieldValue == null) {
				wrappingCell.setValuesAvailable(false);
				return localFieldValue;
			} else {
				// sucess!
				wrappingCell.setValuesAvailable(true);
				JsArray<JsCatalogEntry> regreso = maybeFieldValue.cast();
				return regreso;
			}
		}

	}
	
	

	public static class JsArrayAdapterCell implements Cell<Object> {
		
		static JsArray<JsCatalogEntry> adapter;
		boolean valuesAvailable;
		private Cell<Object> valueCell;
		private Cell<String> keyCell;
		private boolean expectPluralValues;
		static {
			adapter = JavaScriptObject.createArray().cast();
		}

		public JsArrayAdapterCell(Cell<Object> singletonCell,
				Cell<String> simpleKeyCell) {
			super();
			this.valueCell = singletonCell;
			this.keyCell = simpleKeyCell;
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				Object values, SafeHtmlBuilder sb) {
			String key;
			
			if( values!=null && values instanceof String){
				key = (String) values;
				renderKeyValuePair(key, null, sb, context);
			}else if (valuesAvailable && values!=null) {
				JsArray<JsCatalogEntry> entryValues;
				if(expectPluralValues){
					entryValues=(JsArray<JsCatalogEntry>) values;
				}else{
					adapter.set(0, (JsCatalogEntry) values);
					entryValues = adapter;
				}
				
				JsCatalogEntry value;
				for(int i  = 0 ; i < entryValues.length() ; i++){
					value = entryValues.get(i);
					if (value != null) {
						key = value.getId();
						renderKeyValuePair(key, value, sb, context);
					}
				}
			} else if(values!=null){
				JsArrayString stringValues = (JsArrayString) values;
				int length = stringValues.length();
				if(length==0){
					//utterly ignored
				}else{
					// it may be a string array, it may be a number array
					try{
						//try string array first
						key = stringValues.get(0);
						//by this point we are sure it's a string array
						for(int i = 0 ; i < length; i++){
							key = stringValues.get(i);
							renderKeyValuePair(key, null, sb, context);
						}
					}catch(Exception e){
						//if all fails, try a number array
						JsArrayNumber numberValues = (JsArrayNumber) values;
						for(int i = 0 ; i < length; i++){
							key = String.valueOf(numberValues.get(i));
							renderKeyValuePair(key, null, sb, context);
						}
					}
				}
			}


		}

		@Override
		public boolean dependsOnSelection() {
			return false;
		}

		@Override
		public Set<String> getConsumedEvents() {
			return null;
		}

		@Override
		public boolean handlesSelection() {
			return false;
		}

		@Override
		public boolean isEditing(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Object value) {
			return false;
		}

		@Override
		public void onBrowserEvent(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Object value, NativeEvent event,
				ValueUpdater<Object> valueUpdater) {
			// does not handle

		}

		@Override
		public void setValue(com.google.gwt.cell.client.Cell.Context context,
				Element parent, Object value) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			render(context, value, sb);
			parent.setInnerHTML(sb.toSafeHtml().asString());
		}

		@Override
		public boolean resetFocus(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Object value) {
			return false;
		}

		public void renderKeyValuePair(String key, JsCatalogEntry value,
				SafeHtmlBuilder sb,
				com.google.gwt.cell.client.Cell.Context context) {
			sb.appendHtmlConstant("<div>");
			if(value==null){
				try{
					//value may still be able to work! (some cases)
					valueCell.render(context, key, sb);
				}catch(Throwable e){
					keyCell.render(context, key, sb);
				}
			}else{
				try{
					valueCell.render(context, value, sb);
				}catch(Exception e){
					//data that enters this condition should not be passed as a joined value, cuz it's useless to join
					valueCell.render(context, key, sb);
				}
				
			}
			sb.appendHtmlConstant("</div>");
		}


		public void setValuesAvailable(boolean valuesAvailable) {
			this.valuesAvailable = valuesAvailable;
		}


		public void setExpectPluralValues(boolean expectPluralValues) {
			this.expectPluralValues = expectPluralValues;
		}

	}

}
