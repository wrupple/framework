package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class ResizableSelectionGrid<T> extends Composite {

	public interface Tokenizer<T> {
		String getLabel(T p);

		HasClickHandlers getIcon(T p);
	}

	FlexTable table;
	private Tokenizer<T> tokenizer;
	private List<T> renderPending;
	private List<T> items;
	private static final int WIDTH = 220;

	public ResizableSelectionGrid(Tokenizer<T> tokenizer) {
		this.tokenizer = tokenizer;
		table = new FlexTable();
		table.setWidth("100%");
		initWidget(table);
	}

	@Override
	public void onLoad(){
		super.onLoad();
		if(renderPending!=null){
		setData(renderPending);
		}
	}

	public void setData(List<T> result) {
		this.renderPending = null;
		table.removeAllRows();
		int calculatedWidth = 0;
		if (getParent() == null) {
			this.renderPending = result;
			return;
		}
		this.items=result;
		Widget temp = this.getParent();
		while (temp.getOffsetWidth() == 0 && temp.getParent() != null) {
			temp = temp.getParent();
		}
		calculatedWidth = temp.getOffsetWidth();
		if (calculatedWidth == 0) {
			calculatedWidth = Window.getClientWidth();
		}
		int cells = calculatedWidth / (WIDTH + table.getCellSpacing() + table.getCellPadding() + 5);
		if (cells == 0) {
			cells++;
		}
		int initialRow;
		int column;
		T p;
		HasClickHandlers image;
		Label label;
		for (int i = 0; i < result.size(); i++) {
			initialRow = (i / cells) * 2;
			column = i % cells;
			p = result.get(i);
			image = tokenizer.getIcon(p);
			label = new Label(tokenizer.getLabel(p));

			table.setWidget(initialRow, column, (Widget) image);
			table.getCellFormatter().setWidth(initialRow, column, WIDTH + "px");
			initialRow++;
			table.setWidget(initialRow, column, label);

		}
	}

	public T getItem(int arg0) {
		return items.get(arg0);
	}

	public int getItemCount() {
		return items.size();
	}

	public Iterable<T> getItems() {
		return items;
	}

}
