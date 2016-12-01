package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Simple Div displaying arbitrary filtered HTML
 * 
 * 
 * @author japi
 * 
 */
public class RichTextWidget extends Widget implements HasHTML {

	private String unFilteredText;
	private DivElement div;
	private SafeHtmlRenderer<String> renderer;

	public RichTextWidget(String unFilteredText, SafeHtmlRenderer<String> renderer) {
		super();
		if(renderer==null){
			renderer = SimpleSafeHtmlRenderer.getInstance();
		}
		this.renderer=renderer;
		this.div = DivElement.as(DOM.createDiv());
		this.unFilteredText = unFilteredText;
		this.setElement(div);
		div.setInnerHTML(filter(unFilteredText));
	}

	private String filter(String unFilteredText) {
		return renderer.render(unFilteredText).asString();
	}

	@Override
	public String getHTML() {
		return unFilteredText;
	}

	@Override
	public void setHTML(String arg0) {
		unFilteredText = arg0;
		div.setInnerHTML(filter(unFilteredText));
	}

	@Override
	public String getText() {
		return getElement().getInnerText();
	}

	@Override
	public void setText(String arg0) {
		getElement().setInnerText(arg0);
	}


}
