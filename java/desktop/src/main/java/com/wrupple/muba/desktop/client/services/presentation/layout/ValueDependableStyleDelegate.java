package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.google.gwt.dom.client.Element;

public interface ValueDependableStyleDelegate {

	void applyValueStyle(Element element, Object value);
	
	String getCSSAttributes( Object value);
	
	public void setBackgroundColor(String backGroundColor);

	public void setTextColor(String textColor);

}
