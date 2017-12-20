package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.google.gwt.dom.client.Element;

public interface ValueDependableStyleDelegate {

	void applyValueStyle(Element element, Object value);
	
	String getCSSAttributes( Object value);

    void setBackgroundColor(String backGroundColor);

    void setTextColor(String textColor);

}
