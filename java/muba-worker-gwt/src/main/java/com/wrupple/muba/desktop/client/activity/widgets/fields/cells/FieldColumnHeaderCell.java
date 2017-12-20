package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.DateCellProvider.FormattedDateCell;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.TableHeaderData;
import com.wrupple.vegetate.domain.CatalogEntry;

public class FieldColumnHeaderCell extends AbstractCell<TableHeaderData> {

	interface Template extends SafeHtmlTemplates {

		@Template("<option value=\"{0}\" selected=\"selected\">{1}</option>")
		SafeHtml selected(String name, String value);

		@Template("<option value=\"{0}\" >{1}</option>")
		SafeHtml unselected(String name, String value);

		@Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
		SafeHtml input(String value);

		@Template("<input type=\"checkbox\" tabindex=\"-1\" checked/>")
		SafeHtml checked();

		@Template("<input type=\"checkbox\" tabindex=\"-1\" />")
		SafeHtml unchecked();
	}

	private FilterCriteriaFieldDelegate delegate;
	private Template template;
	private JsFieldDescriptor field;

	public FieldColumnHeaderCell(FilterCriteriaFieldDelegate delegate, JsFieldDescriptor field) {
		super("click","blur","change");
		this.delegate = delegate;
		this.template = GWT.create(Template.class);
		this.field = field;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, TableHeaderData value, NativeEvent event,
			ValueUpdater<TableHeaderData> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}

	public TableHeaderData getCurrentInputValue(Element grendpa) {
		// header[ name, operator[select]],valueDiv[valueInput]
		Element header = grendpa.getFirstChildElement();
		Element span = header.getChild(1).cast();
		SelectElement select = span.getFirstChildElement().cast();
		Element valueDiv = header.getNextSiblingElement();
		TableHeaderData regreso = TableHeaderData.createObject().cast();
		regreso.setDescriptor(field);
		Element criteria = valueDiv == null ? null : valueDiv.getFirstChildElement();
		setCriteria(regreso, criteria, select);
		return regreso;
	}

	private void setCriteria(TableHeaderData regreso, Element criteriaElement, SelectElement operatorElement) {

		JsFilterCriteria criteria = JsFilterCriteria.createObject().cast();
		String operator = operatorElement.getOptions().getItem(operatorElement.getSelectedIndex()).getValue();
		criteria.setOperator(operator);
		criteria.pushToPath(field.getFieldId());
		JsArrayMixed values =null;
		if(criteriaElement!=null){
			values = getValues(criteriaElement);
			criteria.setValues(values);
			
		}
		GWT.log("[content table filter] "+operator+""+values);
		regreso.setCriteria(criteria);
	}

	private JsArrayMixed getValues(Element criteriaElement) {
		boolean isKey = field.isKey();
		int dataType = field.getDataType();
		JsArrayMixed criteriaValues = JsArrayMixed.createArray().cast();
		if (isKey) {
			getTextInputValue(criteriaElement, criteriaValues);

		} else {
			switch (dataType) {
			case CatalogEntry.BOOLEAN_DATA_TYPE:
				getCheckboxValue(criteriaElement, criteriaValues);
			case CatalogEntry.DATE_DATA_TYPE:
				getDateInputValue(criteriaElement, criteriaValues);
				break;
			case CatalogEntry.STRING_DATA_TYPE:
				getTextInputValue(criteriaElement, criteriaValues);
				break;
			case CatalogEntry.INTEGER_DATA_TYPE:
			case CatalogEntry.NUMERIC_DATA_TYPE:
				getNumberInputValue(criteriaElement, criteriaValues);
			}
		}
		return criteriaValues;
	}

	private void getCheckboxValue(Element criteriaElement, JsArrayMixed criteriaValues) {
		InputElement e = criteriaElement.cast();
		criteriaValues.push(e.isChecked());
	}

	private void getNumberInputValue(Element criteriaElement, JsArrayMixed criteriaValues) {
		InputElement e = criteriaElement.cast();
		double value = Double.parseDouble(e.getValue());
		criteriaValues.push(value);

	}

	private void getTextInputValue(Element criteriaElement, JsArrayMixed criteriaValues) {
		InputElement e = criteriaElement.cast();
		criteriaValues.push(e.getValue());
	}

	private void getDateInputValue(Element criteriaElement, JsArrayMixed criteriaValues) {
		Window.alert("getDateInputValue:" + criteriaElement.getInnerHTML());
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, TableHeaderData value, SafeHtmlBuilder sb) {
		JsFieldDescriptor field = value.getDescriptor();
		JsFilterCriteria criteria = value.getCriteria();
		sb.appendHtmlConstant("<div class='content-table-header'>");
		sb.appendHtmlConstant("<span class='content-table-header-name'>");
		sb.appendEscaped(field.getName());
		sb.appendHtmlConstant("</span>");

		if (criteria == null) {
			// add button
			renderFilterOperator(null, sb);
			
		} else {
			String operator = criteria.getOperator();
			renderFilterOperator(operator, sb);
			if (operator != null) {
				createFilterValueContainer(context, field, criteria, sb);
			}

		}

	}

	private void renderFilterOperator(String operator, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<span class='content-table-header-criteria'>");
		sb.appendHtmlConstant("<select tabindex=\"-1\">");
		JsArray<JsArrayString> operatorOptions = delegate.getOperatorOptions(field);
		JsArrayString keyValuePair;

		String keyValue;
		String keyName;
		if (operator == null) {
			sb.append(template.unselected("?", "?"));
		}
		for (int i = 0; i < operatorOptions.length(); i++) {
			keyValuePair = operatorOptions.get(i);
			keyName = keyValuePair.get(0);
			keyValue = keyValuePair.get(1);
			if (operator == null) {
				if (i == 0) {
					sb.append(template.unselected(keyValue, keyName));
				} else {
					sb.append(template.unselected(keyValue, keyName));
				}
			} else {

				if (operator.equals(keyValue)) {
					sb.append(template.selected(keyValue, keyName));
				} else {
					sb.append(template.unselected(keyValue, keyName));
				}
			}

		}
		GWT.log("[content table filter render operator]"+operator);
		sb.appendHtmlConstant("</select>");
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("</div>");
	}

	private void createFilterValueContainer(com.google.gwt.cell.client.Cell.Context context, JsFieldDescriptor field, JsFilterCriteria criteria,
			SafeHtmlBuilder sb) {
		
		boolean isKey = field.isKey();
		int dataType = field.getDataType();
		JsArrayMixed criteriaValues = criteria.getValuesArrayOrNull();
		GWT.log("[content table filter render value]"+criteriaValues);
		boolean skipFlag = criteriaValues == null || criteriaValues.length() == 0;
		for (int i = 0; skipFlag || i < criteriaValues.length(); i++) {
			sb.appendHtmlConstant("<div class='content-table-header-value'>");
			if (isKey) {
				if (skipFlag) {
					sb.append(template.input(""));
				} else {
					sb.append(template.input(criteriaValues.getString(i)));
				}

			} else {
				switch (dataType) {
				case CatalogEntry.BOOLEAN_DATA_TYPE:
					if (skipFlag) {
						sb.append(template.unchecked());
					} else {
						sb.append(template.checked());
					}
				case CatalogEntry.DATE_DATA_TYPE:
					doPrintDate(context, field, criteriaValues, sb, i);
					break;
				case CatalogEntry.STRING_DATA_TYPE:
				case CatalogEntry.INTEGER_DATA_TYPE:
				case CatalogEntry.NUMERIC_DATA_TYPE:
					if (skipFlag) {
						sb.append(template.input(""));
					} else {
						sb.append(template.input(criteriaValues.getString(i)));
					}
				}
			}
			sb.appendHtmlConstant("<div class='content-table-header'>");
			if (skipFlag) {
				break;
				// kinda like a do-while
			}
		}

	}

	private void doPrintDate(com.google.gwt.cell.client.Cell.Context context, JsFieldDescriptor field, JsArrayMixed criteriaValues, SafeHtmlBuilder sb, int i) {
		FormattedDateCell cell = getFormattedDateCell(field);
		if (criteriaValues == null) {
			cell.render(context, null, sb);
		} else {
			cell.render(context, criteriaValues.getString(i), sb);
		}

	}

	FormattedDateCell dateCell;

	private FormattedDateCell getFormattedDateCell(JsFieldDescriptor field) {
		if (dateCell == null) {
			DateTimeFormat format = DesktopLoadingStateHolder.getFormat();
			dateCell = new FormattedDateCell(field, new DatePickerCell(format));
		}
		return dateCell;
	}
}
