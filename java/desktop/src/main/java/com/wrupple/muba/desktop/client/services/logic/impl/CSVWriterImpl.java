package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.shared.services.CSVWriter;

public class CSVWriterImpl implements CSVWriter{

	@Override
	public String parseEntryFieldsToCSV(Collection<String> fieldset, List<? extends JavaScriptObject> list) {
		List<String> fields = new ArrayList<String>(fieldset);
		StringBuilder builder = new StringBuilder(list.size()*fieldset.size()*15);
		
		buildCSVHeader(builder,fields);
		builder.append("\r\n");
		int i ;
		String field;
		String value;
		for(JavaScriptObject o:list){
			if(o!=null){
				for(i = 0; i<fields.size();i++){
					field = fields.get(i);
					value = GWTUtils.getAttribute(o,field);
					if(value==null){
						
					}else{
						value = value.replace("\"", "\\\"");
						builder.append("\"");
						builder.append(value);
						builder.append("\"");
					}
					
					if(i==fields.size()-1){
					}else{
						builder.append(',');
					}
				}
				builder.append("\r\n");
			}
		}
		
		return builder.toString();
	}


	private void buildCSVHeader(StringBuilder builder, List<String> fields) {
		String field;
		for(int i = 0; i<fields.size();i++){
			field = fields.get(i);
			builder.append(field);
			if(i==fields.size()-1){
			}else{
				builder.append(',');
			}
		}
	}

}
