package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class UserInteractionToken extends Composite implements HasClickHandlers{

	private static UserInteractionTokenUiBinder uiBinder = GWT
			.create(UserInteractionTokenUiBinder.class);

	interface UserInteractionTokenUiBinder extends
			UiBinder<Widget, UserInteractionToken> {
	}
	

	@UiField
	SpanElement nameSpan;
	@UiField
	SpanElement firstSpan;
	@UiField
	SpanElement lastSpan;
	@UiField
	InlineHTML wrapper;

	public UserInteractionToken() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public SpanElement getNameSpan() {
		return nameSpan;
	}

	public SpanElement getFirstSpan() {
		return firstSpan;
	}

	public SpanElement getLastSpan() {
		return lastSpan;
	}

	public void setBackgroundColor(String rgb){
		getElement().getStyle().setProperty( "backgroundColor", "#"+rgb);
	}
	
	public void setBorderColor(String width, String rgb){
		getElement().getStyle().setProperty( "borderStyle", "solid");
		getElement().getStyle().setProperty( "borderWidth", width);
		if(rgb!=null){
			getElement().getStyle().setProperty( "borderColor", "#"+rgb);
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	
}
