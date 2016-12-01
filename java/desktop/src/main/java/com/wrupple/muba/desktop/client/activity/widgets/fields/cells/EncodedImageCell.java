package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.vegetate.domain.FieldDescriptor;

/**
 * show a base 64 encoded data URL into an image element
 * 
 * @author japi
 *
 */
public class EncodedImageCell extends ProcessDelegatingEditableField<String> {
	
	private final ImageTemplate template;
	
	

	public EncodedImageCell(EventBus bus, ProcessContextServices contextServices, JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,
			Provider<? extends Process<String, String>> nestedProcessProvider, String nestedProcessLocalizedName,ImageTemplate template) {
		super(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, nestedProcessLocalizedName);
		this.template=template;
	}

	@Override
	protected void renderAsInput(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
		renderReadOnly(context,value,sb,viewData);
	}

	@Override
	protected void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
			com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
		if(value==null||value.trim().isEmpty()){
			sb.append(template.noImageOutput());
		}else{
			//FIXME  this is actually unsafe
			SafeUri safeUri = UriUtils.fromTrustedString(value);
			sb.append(template.urlImageOutput(safeUri ));
		}
		
	}

	@Override
	protected String getCurrentInputValue(Element parent, boolean isEditing) {
		Element child = parent.getFirstChildElement();
		if(child==null){
			return null;
		}
		ImageElement e = child.cast();
		String src = e.getSrc();
		if(src!=null){
			if(src.contains("http")){
				return null;
			}
		}
		return src;
	}

}
