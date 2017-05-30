package com.wrupple.muba.desktop.client.activity.widgets.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.DateCellProvider.FormattedDateCell;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ListCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.MapCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbarImpl.FieldFilter;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

public class FieldFilterInteractionToken extends Composite implements FieldFilter {

	private JsFieldDescriptor fieldDescriptor;

	private FlowPanel container;

	private InlineLabel fieldLabel;
	private ListBox operatorPicker;
	private FlowPanel filterContainer;

	protected FieldConversionStrategy conversionService;
	private FilterChangeHandler handler;
	private FilterToolbar parentt;
	private FieldDescriptionService fieldService;

	// private String operator;
	private boolean editableForeignKeys;

	private DesktopTheme theme;

	private ContentManagementSystem cms;

	private ProcessContextServices contextServices;

	boolean ignoreOperatorChanges = false;

	private FilterCriteriaFieldDelegate delegate;

	class OperatorChangeHandler implements ChangeHandler {
		@Override
		public void onChange(ChangeEvent event) {
			int index = operatorPicker.getSelectedIndex();
			String text = operatorPicker.getItemText(index);
			int length = text.length();
			if (length < 4) {
				length = 4;
			}
			operatorPicker.getElement().getStyle().setWidth(length, Unit.EM);
			if (!ignoreOperatorChanges && getValue() != null) {
				handler.onValueChange(null);
			}
		}
	}

	class FilterChangeHandler implements ValueChangeHandler {

		@Override
		public void onValueChange(ValueChangeEvent event) {
			parentt.forceRefreshFromUserData();
		}

	}

	class ShowOptions implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			container.clear();
			// cancel
			InlineLabel cancel = new InlineLabel(fieldLabel.getText());
			cancel.setStyleName(fieldLabel.getStyleName());
			cancel.addClickHandler(new RestoreField());
			container.add(cancel);
			// remove
			Image remove = new Image(theme.delete());
			remove.setStyleName("filterToolbarCriteria-remove");
			remove.addClickHandler(new RemoveThisFilterCriteria());
			container.add(remove);
			// edit
			if (editableForeignKeys) {
				Image edit = new Image(theme.editClear());
				edit.setStyleName("filterToolbarCriteria-edit");
				edit.addClickHandler(new EditForeignKeys());
				container.add(edit);
			}
		}

	}

	class RemoveThisFilterCriteria implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			filterContainer.clear();
			buildContainer();
			parentt.forceRefreshFromUserData();
		}

	}

	class EditForeignKeys implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String foreignCatalog = fieldDescriptor.getForeignCatalogName();
			ContentManager<JsCatalogEntry> cm = cms.getContentManager(foreignCatalog);
			Process<JsTransactionActivityContext, JsTransactionActivityContext> selectionProcess = cm.getSelectionProcess(contextServices, false, false);
			JsTransactionActivityContext input = JsTransactionActivityContext.createObject().cast();
			contextServices.getProcessManager()
					.processSwitch(selectionProcess, foreignCatalog, input, new ForeignSelectionCompletedCallback(), contextServices);
		}

	}

	class RestoreField implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			buildContainer();
		}

	}

	class ForeignSelectionCompletedCallback extends DataCallback<JsTransactionActivityContext> {

		@Override
		public void execute() {
			JsCatalogEntry output = result.getUserOutput();
			if (output != null) {
				JsArray<JsCatalogEntry> regreso = output.cast();
				if (regreso.length() > 0) {
					buildContainer();
					filterContainer.clear();
					JsCatalogKey temp;
					String key;
					Widget filterValueCOntainer;
					for (int i = 0; i < regreso.length(); i++) {
						temp = regreso.get(i);
						key = temp.getId();
						filterValueCOntainer = createFilterValueContainer(key, fieldDescriptor.getDataType(), fieldDescriptor.isKey());
						filterContainer.add((Widget) filterValueCOntainer);
					}
					parentt.forceRefreshFromUserData();
				}
			}
		}

	}

	@Inject
	public FieldFilterInteractionToken(FilterCriteriaFieldDelegate delegate, FieldConversionStrategy conversionService, FieldDescriptionService fieldService,
			DesktopTheme theme, ContentManagementSystem cms) {
		super();
		this.conversionService = conversionService;
		this.cms = cms;
		this.theme = theme;
		this.fieldService = fieldService;
		container = new FlowPanel();
		container.setStyleName("filterToolbarCriteria");
		Style style = container.getElement().getStyle();
		style.setDisplay(Display.INLINE);

		fieldLabel = new InlineLabel();
		fieldLabel.setStyleName("filterToolbarCriteria-label");
		fieldLabel.addClickHandler(new ShowOptions());
		operatorPicker = new ListBox();
		operatorPicker.setStyleName("filterToolbarCriteria-operator");
		operatorPicker.getElement().getStyle().setWidth(4, Unit.EM);
		filterContainer = new FlowPanel();
		filterContainer.setStyleName("filterToolbarCriteria-criteriaWrapper");
		style = filterContainer.getElement().getStyle();
		style.setDisplay(Display.INLINE);
		buildContainer();
		handler = new FilterChangeHandler();
		operatorPicker.addChangeHandler(new OperatorChangeHandler());
		initWidget(container);
		this.delegate = delegate;
	}

	@Override
	public void setValue(FilterCriteria v) {
		filterContainer.clear();
		JsFilterCriteria value = (JsFilterCriteria) v;
		JsArrayMixed criters = value.getValuesArray();

		String operatorWanted = value.getOperator();
		ignoreOperatorChanges = true;
		int catOptions = operatorPicker.getItemCount();
		if (operatorWanted == null) {
			if (catOptions > 0) {
				operatorPicker.setSelectedIndex(0);
			}
		} else {
			String currentItemValue;
			boolean found = false;
			for (int i = 0; i < catOptions; i++) {
				currentItemValue = operatorPicker.getValue(i);
				if (currentItemValue.equals(operatorWanted)) {
					operatorPicker.setSelectedIndex(i);
					found = true;
				}
			}
			if (!found) {
				if (catOptions > 0) {
					operatorPicker.setSelectedIndex(0);
				}
			}
		}
		Widget filterValueCOntainer;
		String filterValue;

		if (criters == null || criters.length() == 0) {
			filterValueCOntainer = createFilterValueContainer("", fieldDescriptor.getDataType(), fieldDescriptor.isKey());
			filterContainer.add((Widget) filterValueCOntainer);
		} else {
			for (int i = 0; i < criters.length(); i++) {
				filterValue = criters.getString(i);
				filterValueCOntainer = createFilterValueContainer(filterValue, fieldDescriptor.getDataType(), fieldDescriptor.isKey());
				filterContainer.add((Widget) filterValueCOntainer);
			}
		}
		ignoreOperatorChanges = false;
	}

	@Override
	public FilterCriteria getValue() {
		int widgetCount = filterContainer.getWidgetCount();
		if (widgetCount == 0) {
			return null;
		} else {
			JsFilterCriteria regreso = JsFilterCriteria.createObject().cast();
			int selectedOperatorIndex = operatorPicker.getSelectedIndex();
			String operator = operatorPicker.getValue(selectedOperatorIndex);
			regreso.setOperator(operator);
			regreso.pushToPath(this.fieldDescriptor.getFieldId());
			JsArrayMixed values = JsArrayMixed.createArray().cast();

			Widget widget;
			for (int i = 0; i < widgetCount; i++) {
				widget = filterContainer.getWidget(i);
				pushFilterCriteriaValueIntoArray(widget, values, this.fieldDescriptor.getDataType(), this.fieldDescriptor.isKey());
			}
			if (values.length() == 0) {
				return null;
			}
			regreso.setValues(values);
			return regreso;
		}

	}

	@Override
	public void initialize(FieldDescriptor f, ProcessContextServices contextServices, FilterToolbar parent) {
		JsFieldDescriptor field = (JsFieldDescriptor) f;
		fieldLabel.setText(field.getName());
		this.parentt = parent;
		this.contextServices = contextServices;
		this.fieldDescriptor = (JsFieldDescriptor) field;
		this.editableForeignKeys = field.isKey() && field.getForeignCatalogName() != null;
		JsArray<JsArrayString> operatorOptions = delegate.getOperatorOptions(field);
		JsArrayString keyValuePair;
		for (int i = 0; i < operatorOptions.length(); i++) {
			keyValuePair = operatorOptions.get(i);
			operatorPicker.addItem(keyValuePair.get(0), keyValuePair.get(1));
		}

	}

	private Widget createFilterValueContainer(String filterValue, int dataType, boolean isKey) {
		HasValue<?> hasValue = null;
		Widget regreso = null;
		if (isKey) {
			regreso = new InlineLabel(filterValue);
		} else {
			switch (dataType) {
			case CatalogEntry.BOOLEAN_DATA_TYPE:
				regreso = new CheckBox();
				Boolean convertedValue = (Boolean) Boolean.parseBoolean(filterValue);
				((CheckBox) regreso).setValue(convertedValue);
				hasValue = (HasValue<?>) regreso;
				break;
			case CatalogEntry.DATE_DATA_TYPE:
				CellWidget<String> date = generateDateicker();
				hasValue = date;
				regreso = date;
				break;
			case CatalogEntry.STRING_DATA_TYPE:
			case CatalogEntry.INTEGER_DATA_TYPE:
			case CatalogEntry.NUMERIC_DATA_TYPE:
				if (fieldDescriptor.getDefaultValueOptionsAsJsArray() != null && fieldDescriptor.getDefaultValueOptionsAsJsArray().length() > 0) {
					CellWidget<String> box = generatePicker();
					hasValue = box;
					regreso = box;
				} else {
					TextBox box = new TextBox();
					box.setValue(filterValue);
					regreso = box;
					box.addValueChangeHandler(new SizeChangeHandler(box));
					hasValue = box;
					break;
				}
			}
		}
		if (hasValue != null) {
			hasValue.addValueChangeHandler(handler);
		}
		if (regreso == null) {
			throw new IllegalArgumentException("unsuported datatype");
		} else {
			return regreso;
		}

	}

	private void pushFilterCriteriaValueIntoArray(Widget regreso, JsArrayMixed values, int dataType, boolean isKey) {
		switch (dataType) {
		case CatalogEntry.BOOLEAN_DATA_TYPE:
			CheckBox hasValue = (CheckBox) regreso;
			boolean value = hasValue.getValue();
			values.push(value);
			break;
		case CatalogEntry.STRING_DATA_TYPE:
			String text;
			if (isKey) {
				InlineLabel label = (InlineLabel) regreso;
				text = label.getText();
			} else {
				HasValue<String> box = (HasValue<String>) regreso;
				text = box.getValue();
			}
			text = text.trim();
			if (!text.isEmpty()) {
				values.push(text);
			}
			break;
		case CatalogEntry.INTEGER_DATA_TYPE:
			if (isKey) {
				InlineLabel label = (InlineLabel) regreso;
				text = label.getText();
			} else {
				HasValue<String> box = (HasValue<String>) regreso;
				text = box.getValue();
			}
			text = text.trim();
			if (!text.isEmpty()) {
				long longValue = Long.parseLong(text);
				if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
					values.push(text);
				} else {
					putNumericValue(text, values);
				}
			}
			break;
		case CatalogEntry.NUMERIC_DATA_TYPE:
			if (isKey) {
				InlineLabel label = (InlineLabel) regreso;
				text = label.getText();
			} else {
				HasValue<String> box = (HasValue<String>) regreso;
				text = box.getValue();
			}
			text = text.trim();
			if (!text.isEmpty()) {
				putNumericValue(text, values);
			}
			break;
		default:
			throw new IllegalArgumentException("unsuported datatype");
		}

	}

	private void buildContainer() {
		container.clear();
		container.add(fieldLabel);
		container.add(operatorPicker);
		container.add(filterContainer);
	}

	private native void putNumericValue(String text, JsArrayMixed values) /*-{
		values[values.length] = Number(text);
	}-*/;

	private CellWidget<String> generateDateicker() {
		return new CellWidget<String>(new FormattedDateCell(fieldDescriptor, new DatePickerCell()));
	}

	private CellWidget<String> generatePicker() {
		Cell<String> cell;
		String widget = fieldDescriptor.getWidget();
		boolean map = "mapPicker".equals(widget);
		if (map) {

			JsArrayString rawOptions = fieldDescriptor.getDefaultValueOptionsAsJsArray();
			List<String> displayValues = new ArrayList<String>(rawOptions.length());
			Map<String, String> valueMap = new HashMap<String, String>();
			String displayValue;
			String systemValue;
			String[] split;
			String rawOption;
			for (int i = 0; i < rawOptions.length(); i++) {
				rawOption = rawOptions.get(i);
				split = rawOption.split("=");
				if (split.length > 1) {
					systemValue = split[0];
					displayValue = split[1];
					displayValues.add(displayValue);
					valueMap.put(displayValue, systemValue);
				}
			}

			cell = new MapCellProvider.MapSelectionCell(displayValues, valueMap);
		} else {
			cell = new ListCellProvider.NormalizedSelectionCell(fieldDescriptor.getDefaultValueOptions(), fieldDescriptor);
		}
		CellWidget<String> box = new CellWidget<String>(cell);
		if (map) {
			String[] split = fieldDescriptor.getDefaultValueOptions().get(0).split("=");
			box.setValue(split[0]);
		} else {
			box.setValue(fieldDescriptor.getDefaultValueOptions().get(0), false);
		}
		return box;
	}

	static class SizeChangeHandler implements ValueChangeHandler<String> {
		TextBox box;

		public SizeChangeHandler(TextBox box) {
			this.box = box;
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			String string = event.getValue();
			if (string == null || string.isEmpty()) {

			} else {
				int size = string.length();
				InputElement element = box.getElement().cast();
				element.setSize(size);
			}
		}

	}

	public void setHideOperator(boolean hideOperator) {
		operatorPicker.setVisible(!hideOperator);
	}

}
