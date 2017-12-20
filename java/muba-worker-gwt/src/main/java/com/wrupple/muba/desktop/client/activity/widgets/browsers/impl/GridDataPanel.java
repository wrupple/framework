package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellBasedWidgetImplStandardBase;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SimpleKeyProvider;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.AbstractRegularGridCellPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionerImpl;
import com.wrupple.muba.desktop.domain.CellPosition;

import java.util.List;

public class GridDataPanel<T> extends AbstractHasData<T> {

	interface CellTemplate extends SafeHtmlTemplates {
		@Template("<div internalindex=\"{5}\" onclick=\"\" class=\"{4}\" style=\" position:absolute; top:{1}px; left:{2}px; width:{3}px; \">{0}</div>")
		SafeHtml gridWrapping(SafeHtml cellContents, int top, int left, int width, String classes,int internalIndex);
	}

	private static final int DEFAULT_PAGE_SIZE = 15;
	private Cell<T> cell;
	private DivElement childContainer;
	private CellBasedWidgetImplStandardBase base;
	private boolean isCellEditing = false;
	private CellTemplate template;

	private int cellSize = 150;
	private int elementsPerRow = 5;
	private AbstractRegularGridCellPositioner positioner;

	public GridDataPanel(Cell<T> cell) {
		this(cell, new SimpleKeyProvider<T>());
	}

	public GridDataPanel(Cell<T> cell, ProvidesKey<T> keyProvider) {
		super(Document.get().createDivElement(), DEFAULT_PAGE_SIZE, keyProvider);
		this.cell = cell;
		this.positioner = new RegularGridCellPositionerImpl(elementsPerRow,cellSize,cellSize);
		template = GWT.create(CellTemplate.class);

		// Add the child container.
		childContainer = Document.get().createDivElement();
		DivElement outerDiv = getElement().cast();
		outerDiv.appendChild(childContainer);

		// Sink events that the cell consumes.
		base = new CellBasedWidgetImplStandardBase();
		base.sinkEvents(this, cell.getConsumedEvents());
	}

	@Override
	protected boolean isKeyboardNavigationSuppressed() {
		return isCellEditing;
	}

	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values, int start,
			SelectionModel<? super T> selectionModel) {

		int keyboardSelectedRow = getKeyboardSelectedRow() + getPageStart();
		int length = values.size();
		int end = start + length;
		StringBuilder classes;
		T value;
		SafeHtmlBuilder cellBuilder;
		Context context;
		boolean isSelected;
		CellPosition position ;
		int x;
		int y;
		for (int i = start; i < end; i++) {
			classes = new StringBuilder();
			value = values.get(i - start);
            isSelected = selectionModel != null && selectionModel.isSelected(value);

			cellBuilder = new SafeHtmlBuilder();
			context = new Context(i, 0, getValueKey(value));
			cell.render(context, value, cellBuilder);

			// a String builder that holds all apropiate class names
			// for the selection, keyboard selection, focus etc...
			if (i == keyboardSelectedRow) {

			}
			if (isSelected) {

			}
			position = positioner.getPosition(i % elementsPerRow, i / elementsPerRow);
			y = (int) position.getY();
			x = (int) position.getX();
			
			sb.append(template.gridWrapping(cellBuilder.toSafeHtml(),y , x, this.cellSize, classes.toString(),i));

		}
	}


	@Override
	protected boolean resetFocusOnCell() {
		int row = getKeyboardSelectedRow();
		if (isRowWithinBounds(row)) {
			Element rowElem = getKeyboardSelectedElement();
			Element cellParent = getCellParent(rowElem);
			T value = getVisibleItem(row);
			Context context = new Context(row + getPageStart(), 0, getValueKey(value));
			return cell.resetFocus(context, cellParent, value);
		}
		return false;
	}

	@Override
	protected void setKeyboardSelected(int index, boolean selected, boolean stealFocus) {
		if (!isRowWithinBounds(index)) {
			return;
		}

		Element elem = getRowElement(index);
		if (!selected || stealFocus) {
			// TODO setStyleName(elem, style.cellListKeyboardSelectedItem(),
			// selected);
		}
		setFocusable(elem, selected);
		if (selected && stealFocus && !isCellEditing) {
			elem.focus();
			onFocus();
		}

	}

	@Override
	protected Element getKeyboardSelectedElement() {
		// Do not use getRowElement() because that will flush the presenter.
		int rowIndex = getKeyboardSelectedRow();
		if (rowIndex >= 0 && childContainer.getChildCount() > rowIndex) {
			return getRowElement(rowIndex);
		}
		return null;
	}

	@Override
	protected boolean dependsOnSelection() {
		return cell.dependsOnSelection();
	}

	@Override
	protected Element getChildContainer() {
		return childContainer;
	}
	
	@Override
	protected void onBrowserEvent2(Event event) {
		EventTarget eventTarget = event.getEventTarget();
	    if (!Element.is(eventTarget)) {
	      return;
	    }
	    
	    //img element
	    final Element target = event.getEventTarget().cast();
	    //div element
	    final Element cellParent = target.getParentElement().getParentElement();
	    
	    
	    String eventType = event.getType();
	    String internalIndexString = null;
	    if("click".equals(eventType)){
	    	internalIndexString = cellParent.getAttribute("internalindex");
	    	if(internalIndexString==null){
	    		return;
	    	}else{
	    		int indexOnPage = Integer.parseInt(internalIndexString);
				T selectedObject = super.getVisibleItem(indexOnPage );
				super.getSelectionModel().setSelected(selectedObject , true);
	    	}
	    }
		
		super.onBrowserEvent2(event);
	}

	/**
	 * Get the parent element that wraps the cell from the list item. Override
	 * this method if you add structure to the element.
	 * 
	 * @param item
	 *            the row element that wraps the list item
	 * @return the parent element of the cell
	 */
	protected Element getCellParent(Element item) {
		return item;
	}

	private Element getRowElement(int rowIndex) {
		return childContainer.getChild(rowIndex).cast();
	}

	public void setElementsPerRow(int elementsPerRow) {
		this.elementsPerRow = elementsPerRow;
	}

	public int getElementsPerRow() {
		return elementsPerRow;
	}

}
