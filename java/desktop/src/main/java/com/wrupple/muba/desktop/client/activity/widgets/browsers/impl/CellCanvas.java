package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.wrupple.muba.desktop.client.services.presentation.layout.CellPositioner;

public class CellCanvas<T> extends AbstractHasData<T> {

	private CellPositioner positioner;

	public CellCanvas(
			ProvidesKey<T> keyProvider,CellPositioner positioner) {
		super(Document.get().createDivElement(), Integer.MAX_VALUE, keyProvider);
		this.positioner=positioner;
	}
	

	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values,
			int start, SelectionModel<? super T> selectionModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean dependsOnSelection() {
		return false;
	}

	@Override
	protected Element getChildContainer() {
		return super.getElement();
	}
	
	@Override
	protected boolean isKeyboardNavigationSuppressed() {
		return true;
	}
	
	@Override
	protected boolean resetFocusOnCell() {
		return false;
	}
	
	@Override
	protected Element getKeyboardSelectedElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setKeyboardSelected(int index, boolean selected,
			boolean stealFocus) {
		// TODO Auto-generated method stub
		
	}

	

}
