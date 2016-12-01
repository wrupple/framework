package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class CustomCell extends AbstractCell<JsCatalogEntry> {
	
	private final String functionName;
	

	public CustomCell(String funtionName) {
		super("click");
		assert funtionName!=null : "Custom Cell does not define a rendering function";
		this.functionName=funtionName;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			JsCatalogEntry value, SafeHtmlBuilder sb) {
		JavaScriptObject renderServices = createRenderServices(sb);
		invoke(value, renderServices, context.getIndex(), context.getColumn(), functionName);
	}
	
	
	private native void invoke(JsCatalogEntry value,JavaScriptObject renderServices,int index, int column,String functionName) /*-{
	  var myFunc = $wnd["render_"+functionName];
	  myFunc(value,renderServices,index,column);
	}-*/;
	
	protected native JavaScriptObject createRenderServices(SafeHtmlBuilder sb) /*-{
		var regreso = {};
		regreso.appendEscaped = $entry(function(x) {
				sb.@com.google.gwt.safehtml.shared.SafeHtmlBuilder::appendEscaped(Ljava/lang/String;)(x);
			});
		regreso.appendEscapedLines = $entry(function(x) {
				sb.@com.google.gwt.safehtml.shared.SafeHtmlBuilder::appendEscapedLines(Ljava/lang/String;)(x);
			});
		regreso.appendTrusted = $entry(function(x) {
				@com.wrupple.muba.desktop.client.activity.widgets.fields.cells.CustomCell::appendTrusted(Ljava/lang/String;Lcom/google/gwt/safehtml/shared/SafeHtmlBuilder;)(x,sb);
			});
		return regreso;
	}-*/;
	
	
	public static void appendTrusted(String string, SafeHtmlBuilder sb){
		SafeHtml html = SafeHtmlUtils.fromTrustedString(string);
		sb.append(html);
	}

}
