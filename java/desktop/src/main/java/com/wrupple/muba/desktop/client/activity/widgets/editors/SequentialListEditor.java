package com.wrupple.muba.desktop.client.activity.widgets.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.factory.help.UserAssistanceProvider;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;

public class SequentialListEditor extends Composite implements
		HasValue<JsArrayString> {

	private class Clicker implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			addTextBox(null);
		}

	}

	private class Finish implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			finish();
		}

	}

	private class ChangeHandler implements FocusHandler, KeyUpHandler {

		private int row;

		public ChangeHandler(int row) {
			this.row = row;
		}

		JavaScriptObject getPropertiesObject() {

			JavaScriptObject regreso = JavaScriptObject.createObject();

			if (list == null || list.isEmpty()) {
			} else {
				String property;
				String value = null;
				String element;
				int detIndex;
				for (int i = 0; i < list.size(); i++) {
					element = list.get(i).getText();
					detIndex = element.indexOf(ServiceBus.propertyDelimiter);
					if (detIndex == 0) {

					} else if (detIndex > 1) {
						int elementLength = element.length();
						if (elementLength == detIndex + 1) {

						} else {
							property = element.substring(0, detIndex);
							value = element.substring(detIndex + 1,
									elementLength);
							GWTUtils.setAttribute(regreso, property, value);
						}
					} else {

					}
				}
			}
			return regreso;
		}

		private void updateSuggestions(JsArray<PropertyValueAvisor> children,
				String currentPropertyName) {
			// TODO cache suggestions (at least where in the tree are we?)
			PropertyValueAvisor advisor;
			JavaScriptObject currentState = getPropertiesObject();
			Set<String> shownKeys = null;
			if (currentPropertyName == null) {
				shownKeys = new HashSet<String>();
			}
			String advisorName;
			for (int i = 0; i < children.length(); i++) {
				advisor = children.get(i);
				if (suggestionStillApplicable(row, advisor, currentState,
						currentPropertyName)) {
					if (currentPropertyName == null) {
						advisorName = advisor.getName();
						if (shownKeys.contains(advisorName)) {

						} else {
							showSuggestion(row, advisor, false);
							shownKeys.add(advisorName);
						}
					} else {
						showSuggestion(row, advisor, true);
					}

				}
			}
		}

		@Override
		public void onFocus(FocusEvent event) {
			clearSuggestions(row);
			JavaScriptObject currentState = getPropertiesObject();
			JsArray<PropertyValueAvisor> advice = JavaScriptObject
					.createArray().cast();
			try{
				rootAdvisor.adviceOnCurrentConfigurationState(currentState, advice);
			}catch(Exception e){
				GWT.log("Failed to complete advice on ListEditor",e);
			}
			String currentPropertyName = indexHasPropertyName();
			updateSuggestions(advice, currentPropertyName);
		}

		private String indexHasPropertyName() {
			String value = list.get(row).getText();
			int index = value.indexOf(ServiceBus.propertyDelimiter);
			if (index > 1) {
				return value.substring(0, index);
			} else {
				return null;
			}
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			onFocus(null);
		}

	}

	private class ApplySuggestion implements ClickHandler {
		int row;
		PropertyValueAvisor advisor;
		boolean showValue;

		public ApplySuggestion(int row, PropertyValueAvisor advisor,
				boolean showValue) {
			this.row = row;
			this.advisor = advisor;
			this.showValue = showValue;
		}

		@Override
		public void onClick(ClickEvent event) {
			TextBox box = list.get(row);
			String value = advisor.getAppliedValue(showValue);
			box.setValue(value);
			box.setFocus(true);
		}

	}

	private final FlexTable table;
	private final List<TextBox> list;
	protected final UserAssistanceProvider rootAdvisor;

	@Inject
	public SequentialListEditor(CatalogUserInterfaceMessages c,
			UserAssistanceProvider rootAdvisor) {
		super();
		this.rootAdvisor = rootAdvisor;
		VerticalPanel main = new VerticalPanel();
		FlowPanel bottom = new FlowPanel();
		Button button = new Button(c.addNew(), new Clicker());
		Button end = new Button(c.ok(), new Finish());
		list = new ArrayList<TextBox>();
		table = new FlexTable();
		main.add(table);
		main.add(bottom);
		bottom.add(end);
		bottom.add(button);
		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidget(main);
		initWidget(scroll);
	}

	private boolean suggestionStillApplicable(int row,
			PropertyValueAvisor advisor, JavaScriptObject currentState,
			String currentPropertyName) {
		if(isOutDated(advisor,currentState)){
			return false;
		}else{
			if (currentPropertyName == null) {
				return testAdvisorName(advisor, list.get(row).getText(), false);
			} else {
				if (testAdvisorName(advisor, currentPropertyName, true)) {
					String currentPropertyValue = GWTUtils.getAttribute(
							currentState, currentPropertyName);
					if (currentPropertyValue == null
							|| testAdvisorValue(advisor, currentPropertyValue)) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
	}

	private boolean isOutDated(PropertyValueAvisor advisor,
			JavaScriptObject currentState) {
		String name = advisor.getName();
		String value = advisor.getValue();
		if(GWTUtils.hasAttribute(currentState, name)){
			if(value==null){
				return true;
			}else{
				String attValue = GWTUtils.getAttribute(currentState, name);
				// TODO test value appllicability? matching rules
				return attValue.equals(value);
			}
		}else{
			return false;
		}
		
	}

	private boolean testAdvisorValue(PropertyValueAvisor advisor, String name) {
		// TODO test value appllicability? matching rules
		String advisorValue = advisor.getValue();
		if (advisorValue == null) {
			return false;
		} else {
			return advisorValue.contains(name) && !advisorValue.equals(name);
		}
	}

	private boolean testAdvisorName(PropertyValueAvisor advisor, String name,
			boolean strict) {
		String advisorName = advisor.getName();
		if (strict) {
			return advisorName.equals(name);
		} else {
			return advisorName.equals(name) || advisorName.contains(name);
		}
	}

	private void clearSuggestions(int row) {
		int cells;
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			cells = table.getCellCount(i);
			if (cells > 1) {
				table.removeCells(i, 1, cells - 1);
			}
		}

	}

	private void showSuggestion(int row, PropertyValueAvisor advisor,
			boolean withValue) {
		Button suggestion = createSuggestionWidget(advisor, withValue);
		suggestion
				.addClickHandler(new ApplySuggestion(row, advisor, withValue));
		int cell = table.getCellCount(row);
		if (cell == 1) {
			// show widget
			table.setWidget(row, 1, new FlowPanel());
		}
		FlowPanel panel = (FlowPanel) table.getWidget(row, 1);
		panel.add(suggestion);
	}

	private Button createSuggestionWidget(PropertyValueAvisor advisor,
			boolean showValue) {
		Button regreso = new Button();
		if (showValue) {
			regreso.setText(advisor.getValue());
		} else {
			regreso.setText(advisor.getName());
		}
		return regreso;
	}

	protected void finish() {
		ValueChangeEvent.fire(this, getValue());
	}

	@Override
	public JsArrayString getValue() {
		JsArrayString regreso = JsArrayString.createArray().cast();
		String value = null;
		for (TextBox box : list) {
			value = box.getValue().trim();
			if (value.length() == 0) {
				// skip
			} else {
				regreso.push(value);
			}
		}
		return regreso;
	}

	@Override
	public void setValue(JsArrayString value) {
		list.clear();
		table.removeAllRows();
		if (value != null) {
			String s;
			for (int i = 0; i < value.length(); i++) {
				s = value.get(i);
				addTextBox(s);
			}
		}
	}

	private void addTextBox(String s) {
		TextBox temp = new TextBox();
		int rows = 0;
		if (s != null) {
			temp.setValue(s);
		}
		list.add(temp);
		rows = table.getRowCount();
		table.setWidget(rows, 0, temp);
		if (rootAdvisor != null) {
			ChangeHandler handler = new ChangeHandler(rows);
			temp.addKeyUpHandler(handler);
			// temp.addBlurHandler(handler);
			temp.addFocusHandler(handler);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JsArrayString> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(JsArrayString value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, getValue());
		}
	}

}
