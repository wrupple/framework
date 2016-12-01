package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class CenteredPanel extends ResizeComposite {

	private static CenteredPanelUiBinder uiBinder = GWT.create(CenteredPanelUiBinder.class);

	interface CenteredPanelUiBinder extends UiBinder<Widget, CenteredPanel> {
	}

	@UiField(provided = true)
	Widget contained;

	public CenteredPanel(Widget contained, int width) {
		contained.setWidth(width+"px");
		contained.setHeight("100%");
		this.contained=contained;
		initWidget(uiBinder.createAndBindUi(this));
	}

}
