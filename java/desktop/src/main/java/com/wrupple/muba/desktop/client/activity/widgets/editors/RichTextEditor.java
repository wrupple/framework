package com.wrupple.muba.desktop.client.activity.widgets.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RichTextArea;

public class RichTextEditor extends Composite  implements HasValue<String>{
	private RichTextArea area;

	public RichTextEditor() {
		super();
		area = new RichTextArea();
		area.ensureDebugId("cwRichText-area");
		area.setSize("100%", "100%");
		RichTextToolbar toolbar = new RichTextToolbar(area);
		toolbar.ensureDebugId("cwRichText-toolbar");
		toolbar.setWidth("100%");

		// Add the components to a panel
		Grid grid = new Grid(2, 1);
		grid.setWidth("100%");
		grid.setStyleName("cw-RichText");
		grid.setWidget(0, 0, toolbar);
		grid.setWidget(1, 0, area);
		this.initWidget(grid);
	}

	public RichTextArea getTextArea() {
		return this.area;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return this.area.getHTML();
	}

	@Override
	public void setValue(String value) {
		this.area.setHTML(SimpleSafeHtmlRenderer.getInstance().render(value));
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		setValue(value);
		if(fireEvents){
			ValueChangeEvent.fire(this, value);
		}
	}

}
