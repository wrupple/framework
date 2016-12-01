package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;

@SuppressWarnings("serial")
public  final  class JsWruppleDomainHTMLPage extends JsCatalogEntry implements WruppleDomainHTMLPage {

	protected JsWruppleDomainHTMLPage() {
		super();
	}

	@Override
	public native void setValue(String string) /*-{
		this.value=string;
}-*/;

	@Override
	public String getValue() {
		return getStringValue();
	}

}
