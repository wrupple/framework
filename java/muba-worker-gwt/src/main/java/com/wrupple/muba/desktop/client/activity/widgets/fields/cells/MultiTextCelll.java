package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.ClickableElementTemplate;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.InlineText;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public class MultiTextCelll extends ProcessDelegatingEditableField<JsArrayString> {

	private InlineText inlineText;
	private ClickableElementTemplate button;
	private CatalogUserInterfaceMessages messages;


	public MultiTextCelll(EventBus bus,
			ProcessContextServices contextServices,
			JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode,
			Provider<Process<JsArrayString, JsArrayString>> nestedProcess,
			String nestedProcessLocalizedName,ClickableElementTemplate button,CatalogUserInterfaceMessages msgs) {
		super(bus, contextServices, contextParameters, d, mode, nestedProcess,
				nestedProcessLocalizedName);
		inlineText = GWT.create(InlineText.class);
		this.button = button;
		messages = msgs;
	}

	@Override
	protected void renderAsInput(com.google.gwt.cell.client.Cell.Context context, JsArrayString v, SafeHtmlBuilder sb,
			FieldData<JsArrayString> viewData) {
		if (v != null) {
			renderValues(v, sb);
		}
		sb.append(button.button(messages.addNew()));
	}

	@Override
	protected void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, JsArrayString v, SafeHtmlBuilder sb,
			FieldData<JsArrayString> viewData) {
		if (v == null) {
			return;
		}
		if (v != null) {
			renderValues(v, sb);
		}
	}

	@Override
	protected JsArrayString getCurrentInputValue(Element parent, boolean isEditing) {
		JsArrayString regreso = JsArrayString.createArray().cast();
		NodeList<Node> children = parent.getChildNodes();
		Node node;
		DivElement output;
		String value;
		for (int i = 0; i < children.getLength(); i++) {
			node =children.getItem(i);
			
			if(node!=null){
				output = node.cast();
				value = output.getInnerText();
				if (value == null || value.length() == 0) {
				} else {
					regreso.push(value.trim());
				}
			}
		}
		if(regreso.length()==0){
			return null;
		}else{
			return regreso;
		}
	}

	
	private void renderValues(JsArrayString v, SafeHtmlBuilder sb) {
		String current;
		for ( int i = 0 ; i < v.length(); i++) {
			current =v.get(i);
			sb.appendHtmlConstant("<div>");
			sb.append(inlineText.output(current));
			sb.appendHtmlConstant("</div>");
		}
	}

}
