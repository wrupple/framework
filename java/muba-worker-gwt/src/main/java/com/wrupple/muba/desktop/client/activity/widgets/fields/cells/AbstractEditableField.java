package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.TextInputCell.ViewData;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData;
import com.wrupple.vegetate.domain.FieldDescriptor;

public abstract class AbstractEditableField<T> extends AbstractEditableCell<T, FieldData<T>> {


	/**
	 * The view data object used by this cell. We need to store both the text
	 * and the state because this cell is rendered differently in edit mode. If
	 * we did not store the edit state, refreshing the cell with view data would
	 * always put us in to edit state, rendering a text box instead of the new
	 * text string.
	 */
	public static class FieldData<T> {

		private boolean isEditing;

		/**
		 * If true, this is not the first edit.
		 */
		private boolean editedBefore;

		/**
		 * Keep track of the original value at the start of the edit, which
		 * might be the edited value from the previous edit and NOT the actual
		 * value.
		 */
		private T original;

		private T currentValue;

		/**
		 * Construct a new ViewData in editing mode.
		 * 
		 * @param text
		 *            the text to edit
		 */
		public FieldData(T text) {
			this.original = text;
			this.currentValue = text;
			this.isEditing = true;
			this.editedBefore = false;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FieldData other = (FieldData) obj;
			if (currentValue == null) {
				if (other.currentValue != null)
					return false;
			} else if (!currentValue.equals(other.currentValue))
				return false;
			if (editedBefore != other.editedBefore)
				return false;
			if (isEditing != other.isEditing)
				return false;
			if (original == null) {
				if (other.original != null)
					return false;
			} else if (!original.equals(other.original))
				return false;
			return true;
		}

		public T getOriginal() {
			return original;
		}

		public T getCurrentValue() {
			return currentValue;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((currentValue == null) ? 0 : currentValue.hashCode());
			result = prime * result + (editedBefore ? 1231 : 1237);
			result = prime * result + (isEditing ? 1231 : 1237);
			result = prime * result + ((original == null) ? 0 : original.hashCode());
			return result;
		}

		public boolean isEditing() {
			return isEditing;
		}

		public boolean isEditedBefore() {
			return editedBefore;
		}

		public void setEditing(boolean isEditing) {
			boolean wasEditing = this.isEditing;
			this.isEditing = isEditing;

			// This is a subsequent edit, so start from where we left off.
			if (!wasEditing && isEditing) {
				editedBefore = true;
				original = currentValue;
			}
		}

		public void setCurrentValue(T text) {
			this.currentValue = text;
		}

	}



	protected final FieldDescriptor fieldDescriptor;
	protected final CatalogAction mode;
	boolean allowEdit;
	/*
	 * POPUP panel metadata
	 */
	private Object lastKey;
	private Element lastParent;
	private int lastIndex;
	private int lastColumn;
	private ValueUpdater<T> valueUpdater;
	/**
	 * When true, all events will be ignored
	 */
	public boolean lockAllEvents;
	

	public AbstractEditableField(FieldDescriptor d, CatalogAction mode) {
		super("click", "keyup", "keydown", "blur");
		this.fieldDescriptor = d;
		this.mode=mode;
		this.allowEdit=mode != CatalogAction.READ;
	}


	protected void finishEditing(T value) {
		Object key = lastKey;
		FieldData<T> viewData = getViewData(key);
		
		//TODO this assertion makes sense but was giving so much trouble...
		//assert viewData != null && viewData.isEditing() : "Attempted to exit edit mode without actually ever entering edit mode";
		//
		// Remember the values before hiding the popup.
		Element cellParent = lastParent;

		int index = lastIndex;
		int column = lastColumn;

		// Update the cell and value updater.
		T newValue = value;
		// should not be null ... ... i think
		viewData.setEditing(false);
		viewData.setCurrentValue(newValue);
		setViewData(key, viewData);
		setValue(new Context(index, column, key), cellParent, newValue);
		if (valueUpdater != null) {
			valueUpdater.update(newValue);
		}
		lastKey = null;
		lastParent = null;
		lastIndex = -1;
		lastColumn = -1;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		FieldData<T> viewData = getViewData(key);
		if (viewData != null && !viewData.isEditing() && value != null && value.equals(viewData.getCurrentValue())) {
			clearViewData(key);
			viewData = null;
		}

		if (allowEdit) {
			viewData = new FieldData<T>(value);
			setViewData(key, viewData);
		}

		if (viewData != null) {
			if (allowEdit && viewData.isEditing()) {
				renderAsInput(context, value, sb, viewData);
			} else {
				renderReadOnly(context, value, sb, viewData);
			}
		} else if (value != null) {
			renderReadOnly(context, value, sb, viewData);
		}
	}

	@Override
	public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, T value) {
		FieldData<T> viewData = getViewData(context.getKey());
        return viewData != null && viewData.isEditing();
    }

	@Override
	public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		if (lockAllEvents) {
			return;
		}
		this.valueUpdater = valueUpdater;
		Object key = context.getKey();
		FieldData<T> viewData = getViewData(key);
		if (viewData != null && viewData.isEditing()) {
			// Handle the edit event.
			editEvent(context, parent, value, viewData, event, valueUpdater);
		} else {
			String type = event.getType();
			int keyCode = event.getKeyCode();
			boolean enterPressed = "keyup".equals(type) && keyCode == KeyCodes.KEY_ENTER;
			if ("click".equals(type) || enterPressed) {
				// Go into edit mode.
				if (viewData == null) {
					viewData = new FieldData<T>(value);
					setViewData(key, viewData);
				} else {
					viewData.setEditing(true);
				}
				edit(context, parent, value, viewData);
			}
		}
	}


	private void editEvent(Context context, Element parent, T value, FieldData<T> viewData, NativeEvent event,
			ValueUpdater<T> valueUpdater) {
		lastKey = context.getKey();
		lastParent = parent;
		lastIndex = context.getIndex();
		lastColumn = context.getColumn();
		onEditModeEvent(context, parent, value, viewData, event, valueUpdater);
		String type = event.getType();
		boolean keyUp = "keyup".equals(type);
		boolean keyDown = "keydown".equals(type);
		boolean click = "click".equals(type);
		if (click) {
			//showDelegate(context, parent, value, event, valueUpdater);
		} else if (keyUp || keyDown) {
			int keyCode = event.getKeyCode();
			if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
				commit(context, parent, viewData, valueUpdater);
			} else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
				// Cancel edit mode.
				T originalText = viewData.getOriginal();
				if (viewData.isEditedBefore()) {
					viewData.setCurrentValue(originalText);
					viewData.setEditing(false);
				} else {
					setViewData(context.getKey(), null);
				}
				cancel(context, parent, value, viewData);
			} else {
				// Update the value in the view data on each key.
				updateViewData(parent, viewData, true);
			}
		} else if ("blur".equals(type)) {
			// Commit the change. Ensure that we are blurring the input element
			// and
			// not the parent element itself.
			EventTarget eventTarget = event.getEventTarget();
			if (Element.is(eventTarget)) {
				Element target = Element.as(eventTarget);
				if ("input".equals(target.getTagName().toLowerCase())) {
					commit(context, parent, viewData, valueUpdater);
				}
			}
		}
	}

	private void edit(com.google.gwt.cell.client.Cell.Context context, Element parent, T value, FieldData<T> viewData) {
		setValue(context, parent, value);
		onWillEnterEditMode(parent, viewData);
	}

	private void cancel(com.google.gwt.cell.client.Cell.Context context, Element parent, T value, FieldData<T> viewData) {
		onValueWillCommit(parent, viewData);
		setValue(context, parent, value);
	}

	private void commit(Context context, Element parent, FieldData<T> viewData, ValueUpdater<T> valueUpdater) {
		T value = updateViewData(parent, viewData, false);
		onValueWillCommit(parent, viewData);
		setValue(context, parent, viewData.getOriginal());
		if (valueUpdater != null) {
			valueUpdater.update(value);
		}
	}

	/**
	 * Update the view data based on the current value.
	 * 
	 * @param parent
	 *            the parent element
	 * @param viewData
	 *            the {@link ViewData} object to update
	 * @param isEditing
	 *            true if in edit mode
	 * @param key
	 * @return the new value
	 */
	private T updateViewData(Element parent, FieldData<T> viewData, boolean isEditing) {
		T value = getCurrentInputValue(parent,  isEditing);
		viewData.setCurrentValue(value);
		viewData.setEditing(isEditing);
		return value;
	}


	protected void onEditModeEvent(Context context, Element parent, T value, FieldData<T> viewData, NativeEvent event,
			ValueUpdater<T> valueUpdater) {
	}

	/**
	 * render a read only cell
	 * 
	 * @param context
	 * @param value
	 * @param sb
	 * @param viewData
	 */
	protected abstract void renderAsInput(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb,
			FieldData<T> viewData);

	/**
	 * Render the cell in an editable form
	 * 
	 * @param context
	 * @param value
	 * @param sb
	 * @param viewData
	 */
	protected abstract void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, T value,
			SafeHtmlBuilder sb, FieldData<T> viewData);

	/**
	 * Return user's input as a javascript object
	 * 
	 * @param parent
	 * @param key
	 * @param isEditing
	 * @return
	 */
	protected abstract T getCurrentInputValue(Element parent, boolean isEditing);

	/**
	 * Clear selection, or do other cleanup operations before the value commits
	 * 
	 * @param parent
	 */
	protected abstract void onValueWillCommit(Element parent, FieldData<T> viewData);

	/**
	 * Focus/blur/select or change the state of rendered input elements
	 * 
	 * @param parent
	 */
	protected abstract void onWillEnterEditMode(Element parent, FieldData<T> viewData);
	
	

}
