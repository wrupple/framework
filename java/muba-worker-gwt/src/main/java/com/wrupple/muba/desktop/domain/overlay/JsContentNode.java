package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;

import java.util.Date;

@SuppressWarnings("serial")
public class JsContentNode extends JsCatalogEntry implements ContentNode {

	protected JsContentNode() {
    }

    @Override
    public final Date getTimestamp() {
		String rar = getRawTimestamp();
		if (rar == null) {
			return null;
		}
		return DesktopLoadingStateHolder.getFormat().parse(rar);
	}

	private final native String getRawTimestamp() /*-{
		return this.timestamp;
	}-*/;

	@Override
	public final void setTimestamp(Date d) {
		if (d == null) {
			setRawTimestamp(null);
		}
		setRawTimestamp(DesktopLoadingStateHolder.getFormat().format(d));
	}

	public final native void setRawTimestamp(String format) /*-{
		this.timestamp = format;
	}-*/;

	public final native JsArray<JsContentNode> getChildrenValues() /*-{
		return this.childrenValues;
	}-*/;

	public final native double getValueAsDouble() /*-{
		return this.value;
	}-*/;

	public final native JsArrayString getChildren() /*-{
		return this.children;
	}-*/;

	public final native void setChildrenValues(JsArray<JsContentNode> childrenValues) /*-{
		this.childrenValues=childrenValues;
	}-*/;

}
