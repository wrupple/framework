package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.process.impl.FilterCriteriaValueInputProcess;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleActivityToolbarBase;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.services.logic.ModifyUserInteractionStateModelCommand;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.client.services.logic.URLFilterDataSerializationService;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.ModelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

/**
 * 
 * Load Catalog Descriptor FilterOptionSelectionViewImpl
 * 
 * 
 * @author japi (tempt me not)
 * 
 */
public class FilterToolbarImpl extends WruppleActivityToolbarBase implements FilterToolbar {

	public interface FieldFilter extends TakesValue<FilterCriteria>, IsWidget {
		void initialize(FieldDescriptor field, ProcessContextServices contextServices, FilterToolbar parent);

		void setHideOperator(boolean hideOperatorSymbol);
	}

	class NewCriteriaAddition implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			ProcessManager pm = contextServices.getProcessManager();
			FilterCriteriaValueInputProcess process = criteriaCreationProcessProvider.get();
			pm.processSwitch(process, "Select Filters", catalogId, new CriteriaCreatedCallback(), contextServices);
		}

	}

	class CriteriaCreatedCallback extends DataCallback<JsFilterCriteria> {

		@Override
		public void execute() {
			if (result != null) {
				cds.loadCatalogDescriptor(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), catalogId, new AddCriteria(result));
			}
		}

	}

	class AddCriteria extends DataCallback<CatalogDescriptor> {
		JsFilterCriteria criteria;

		public AddCriteria(JsFilterCriteria result) {
			this.criteria = result;
		}

		@Override
		public void execute() {
			boolean callRefreshEvents = readReadRefreshData(criteria);
			FieldDescriptor field = result.getFieldDescriptor(criteria.getPath(0));
			boolean isValidFilterableField = isValidFilterableCriteria(criteria, field, result);
			if (isValidFilterableField) {
				addCriteria(criteria, field, callRefreshEvents);
			}
		}

	}

	/*
	 * Services
	 */
	private StorageManager cds;
	private ServiceBus serviceBus;
	private DesktopTheme theme;
	private Provider<FieldFilter> widgetProvider;
	private Provider<FilterCriteriaValueInputProcess> criteriaCreationProcessProvider;
	/*
	 * Widgets
	 */
	private final FlowPanel container;
	private final Image addCriteriaButton;
	private final InlineLabel titleLabel;
	/*
	 * State
	 */
	private JsFilterData currentValue;
	private String modelAlterationTarget;
	private String catalogId;
	private URLFilterDataSerializationService filterDataSerializer;
	private boolean hideOperatorSymbol;
	private HashSet<String> visibleFields;

	@Inject
	public FilterToolbarImpl(URLFilterDataSerializationService filterDataSerializer, StorageManager cds, DesktopTheme theme,
			Provider<FilterCriteriaValueInputProcess> criteriaCreationProcessProvider, Provider<FieldFilter> widgetProvider, ServiceBus serviceBus,
			ToolbarMap toolbarMap) {
		super(toolbarMap);
		this.theme = theme;
		this.filterDataSerializer = filterDataSerializer;
		this.cds = cds;
		this.criteriaCreationProcessProvider = criteriaCreationProcessProvider;
		container = new FlowPanel();
		container.setStyleName("filterToolbar");
		SimpleLayoutPanel wrapper = new SimpleLayoutPanel();
		wrapper.setStyleName("filterToolbar-wrapper");
		wrapper.setWidget(container);
		initWidget(wrapper);
		this.widgetProvider = widgetProvider;
		this.serviceBus = serviceBus;

		addCriteriaButton = new Image(theme.filterIcon());
		addCriteriaButton.setStyleName("filterToolbar-addButton");
		Style style = addCriteriaButton.getElement().getStyle();
		style.setDisplay(Display.INLINE);
		addCriteriaButton.addClickHandler(new NewCriteriaAddition());
		FlowPanel mainFilterControls = new FlowPanel();
		mainFilterControls.setStyleName("filterToolbar-controls");
		mainFilterControls.add(addCriteriaButton);
		titleLabel = new InlineLabel();
		mainFilterControls.add(titleLabel);
		container.add(mainFilterControls);
	}

	@Override
	public void addCriteria(FilterCriteria result, FieldDescriptor field, boolean fireEvents) {
		if (result != null) {
			FieldFilter widget = widgetProvider.get();
			widget.setHideOperator(hideOperatorSymbol);
			widget.initialize(field, contextServices, this);
			widget.setValue(result);
			container.add(widget);

			if (visibleFields != null) {
				widget.asWidget().setVisible(visibleFields.contains(field.getFieldId()));
			}
			if (fireEvents) {
				forceRefreshFromUserData();
			}
		}
	}

	@Override
	public JsFilterData getValue() {
		currentValue = JsFilterData.newFilterData();
		JsArray<JsFilterCriteria> filters = extractCurrentFilters();
		currentValue.setFiltersArray(filters);
		return currentValue;
	}

	@Override
	public void setValue(JavaScriptObject value) {
		if (value == null) {
			value = JsFilterData.newFilterData();
		}
		JsFilterData newValue = value.cast();
		setNewValue(newValue, true);
	}

	private void setNewValue(JsFilterData newValue, final boolean fireEvents) {
		this.currentValue = newValue.cast();
		cds.loadCatalogDescriptor(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), catalogId, new DataCallback<CatalogDescriptor>() {

			@Override
			public void execute() {
				clearWidgetsAndDisplayNewValue(currentValue.getFilters(), result);
				if (fireEvents) {
					forceRefreshFromUserData();
				}
			}
		});
	}

	public void forceRefreshFromUserData() {
		JsFilterData newValue = getValue();
		ModelTransformationConfig commandProperties = ModelTransformationConfig.createObject().cast();
		commandProperties.setSourceData(newValue);
		commandProperties.setTarget(modelAlterationTarget);
		StateTransition<JsTransactionApplicationContext> callback = DataCallback.nullCallback();
		serviceBus.excecuteCommand(ModifyUserInteractionStateModelCommand.COMMAND, commandProperties, eventBus, contextServices, contextParameters, callback);

		String unencodedString;
		try {
			unencodedString = filterDataSerializer.serialize(newValue);
			contextServices.getDesktopManager().putPlaceParameter(CatalogActionRequest.FILTER_DATA_PARAMETER, unencodedString);
		} catch (Exception e) {
			GWT.log("Unable to update url filters", e);
		}
	}

	private JsArray<JsFilterCriteria> extractCurrentFilters() {
		FieldFilter widget;
		FilterCriteria e;
		JsArray<JsFilterCriteria> regreso = JavaScriptObject.createArray().cast();
		List<Widget> removable = null;
		for (Widget w : container) {
			if (addCriteriaButton != w) {
				widget = (FieldFilter) w;
				e = widget.getValue();
				if (e == null) {
					if (removable == null) {
						removable = new ArrayList<Widget>();
					}
					removable.add(w);
				} else {
					regreso.push((JsFilterCriteria) e);
				}
			}
		}
		if (removable != null) {
			for (Widget w : removable) {
				container.remove(w);
			}
		}

		return regreso;
	}

	private void clearWidgetsAndDisplayNewValue(List<? extends FilterCriteria> criters, CatalogDescriptor catalog) {
		container.clear();
		if (this.addCriteriaButton != null) {
			container.add(addCriteriaButton);
		}
		FieldDescriptor fieldDescriptor;
		for (FilterCriteria field : criters) {
			fieldDescriptor = catalog.getFieldDescriptor(field.getPath(0));
			if (isValidFilterableCriteria(field, fieldDescriptor, catalog)) {
				addCriteria(field, fieldDescriptor, false);
			}
		}
	}

	public static boolean isValidFilterableCriteria(FilterCriteria criteria, FieldDescriptor field, CatalogDescriptor catalog) {
		return field != null && field.isFilterable();
	}

	private native boolean readReadRefreshData(JsFilterCriteria criteria) /*-{
		return criteria.preventDataViewRefreshingWhenAdded == null;
	}-*/;

	public static native void setRefreshData(JsFilterCriteria criteria, boolean refresh) /*-{
		if (!refresh) {
			criteria.preventDataViewRefreshingWhenAdded = false;
		}
	}-*/;

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		super.initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);
		this.catalogId = parameter.getCatalogId();

		JsFilterData startupValue = contextParameters.getFilterData();
		assert startupValue != null;
		setNewValue(startupValue, false);
	}

	@Override
	public void setModelAlterationTarget(String s) {
		this.modelAlterationTarget = s;
	}
	@Override
	public void setHideNewCriteriaButton(String s) {
		if (Boolean.parseBoolean(s)) {
			addCriteriaButton.setVisible(false);
		}
	}
	@Override
	public void setHideOperatorSymbol(String s) {
		this.hideOperatorSymbol = Boolean.parseBoolean(s);
	}
	@Override
	public void setLabel(String s) {
		titleLabel.setText(s);
	}
	@Override
	public void setVisibleFields(String s) {
		if (s != null) {
			String[] fields = s.split(",");
			this.visibleFields = new HashSet<String>(fields.length);
			for (String field : fields) {
				visibleFields.add(field);
			}
		}
	}

}
