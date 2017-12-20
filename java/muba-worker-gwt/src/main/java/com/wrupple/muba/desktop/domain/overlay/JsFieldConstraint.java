package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.vegetate.domain.Constraint;

@SuppressWarnings("serial")
public class JsConstraint extends JsCatalogEntry implements Constraint {
	
	protected JsConstraint(){}

	@Override
	public final native String getConstraint() /*-{
		return this.constraint;
	}-*/;


}
