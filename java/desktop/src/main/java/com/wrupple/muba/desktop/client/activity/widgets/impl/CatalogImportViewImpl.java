package com.wrupple.muba.desktop.client.activity.widgets.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.widgets.ImportView;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;

public class CatalogImportViewImpl extends Composite implements
		ImportView {

	private static CatalogImportViewImplUiBinder uiBinder = GWT
			.create(CatalogImportViewImplUiBinder.class);

	interface CatalogImportViewImplUiBinder extends
			UiBinder<Widget, CatalogImportViewImpl> {
	}


	@UiField
	TextArea textArea;
	@UiField
	HorizontalPanel toolbar;
	@Inject
	public CatalogImportViewImpl(CatalogUserInterfaceMessages cc) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getCsv() {
		return textArea.getValue();
	}

	@Override
	public void addActions(List<Image> actions) {
		if(actions!=null){
			for(Image i: actions){
				toolbar.add(i);
			}
		}
	}
	


}
