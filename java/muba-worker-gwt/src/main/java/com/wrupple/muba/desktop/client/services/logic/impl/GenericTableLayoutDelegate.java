package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.*;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateDataTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.IndexedContentTable;
import com.wrupple.muba.desktop.client.activity.widgets.fields.column.FieldColumn;
import com.wrupple.muba.desktop.client.activity.widgets.fields.column.FieldColumnHeader;
import com.wrupple.muba.desktop.client.activity.widgets.fields.column.ForeignEntryColumn;
import com.wrupple.muba.desktop.client.activity.widgets.fields.column.ForeignEntryColumn.JsArrayAdapterCell;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils.DataGroup;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GenericTableLayoutDelegate implements TableLayoutDelegate {

	private CatalogFieldMap fieldFactory;
	private FieldConversionStrategy conversionService;
	private StorageManager descriptionService;
	private FilterCriteriaFieldDelegate headerDelegate;

	public static final String GENERIC_KEY_CELL_WIDGETID = "genericKey";

	// public static final String GENERIC_VALUE_CELL_WIDGETID = "genericValue";

	class EphemeralColumnCallback extends DataCallback<CatalogDescriptor> {
		FieldDescriptor localCatalogField;
		JsCatalogKey entry;
		CatalogDescriptor catalog;
		AbstractCellTable<JsCatalogEntry> display;
		EventBus bus;
		ProcessContextServices contextServices;
		JsTransactionApplicationContext contextParameters;
		JavaScriptObject browserDescriptor;
		private IndexedContentTable table;

		public EphemeralColumnCallback(IndexedContentTable table, FieldDescriptor localCatalogField, JsCatalogKey entry, CatalogDescriptor catalog,
				AbstractCellTable<JsCatalogEntry> display, EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
				JavaScriptObject browserDescriptor) {
			super();
			this.localCatalogField = localCatalogField;
			this.entry = entry;
			this.catalog = catalog;
			this.display = display;
			this.bus = bus;
			this.contextServices = contextServices;
			this.contextParameters = contextParameters;
			this.browserDescriptor = browserDescriptor;
			this.table = table;
		}

		@Override
		public void execute() {
			if (result != null) {
				FieldDescriptor field;
				// price
				field = localCatalogField;
				FieldDescriptor pathField;
				JsArrayString path = null;
				List<FilterCriteria> includeCriteria;
				String catalogId = entry.getCatalog();
				String name = entry.getName();
				pathField = findMatchingField(result, catalogId);
				if (pathField != null) {
					// retailer
					path = JavaScriptObject.createArray().cast();
					path.push(pathField.getFieldId());
				}
				includeCriteria = selectionIncludeCriteria(path, entry.getId());
				if (includeCriteria != null) {
					addColumn(name, display, field, catalog, includeCriteria, bus, contextServices, contextParameters, browserDescriptor, table);
				}
			}
		}

	}

	@Inject
	public GenericTableLayoutDelegate(FilterCriteriaFieldDelegate headerDelegate, StorageManager descriptionService, CatalogFieldMap fieldFactory,
			FieldDescriptionService descriptor, FieldConversionStrategy conversionService) {
		super();
		this.headerDelegate = headerDelegate;
		this.descriptionService = descriptionService;
		this.fieldFactory = fieldFactory;
		this.conversionService = conversionService;
	}

	@Override
	public void initialize(String host, String domain, CatalogDescriptor catalog,
                           Collection<FieldDescriptor> summaries, JsArray<JsCatalogEntry> selections, AbstractCellTable<JsCatalogEntry> display, EventBus bus,
                           ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters, JavaScriptObject browserDescriptor, final IndexedContentTable table) {

		table.addValueChangeHandler(new ValueChangeHandler<JsFilterData>() {
			@Override
			public void onValueChange(ValueChangeEvent<JsFilterData> event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						//redraw to allow headers to render their new values (if applicable)
						table.getUnderlyingTable().redrawHeaders();
					}
				});
			}
		});
		for (FieldDescriptor field : summaries) {
			addColumn(null, display, field, catalog, null, bus, contextServices, contextParameters, browserDescriptor, table);
		}
		if (selections != null) {
			JsCatalogKey entry;
			Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
			String foreignCatalog;

			for (int i = 0; i < selections.length(); i++) {
				// a retailer
				entry = selections.get(i);
				// pricesValues
				for (FieldDescriptor localCatalogField : fields) {
					foreignCatalog = localCatalogField.getForeignCatalogName();
					if (foreignCatalog != null) {
						// PriceEntry
						descriptionService.loadCatalogDescriptor(host, domain, foreignCatalog, new EphemeralColumnCallback(table, localCatalogField, entry, catalog, display,
								bus, contextServices, contextParameters, browserDescriptor));

					}
				}

			}
		}

	}

	private FieldDescriptor findMatchingField(CatalogDescriptor catalog, String entryCatalog) {
		if (entryCatalog != null) {
			Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
			for (FieldDescriptor field : fields) {
				if (entryCatalog.equals(field.getForeignCatalogName())) {
					return field;
				}
			}
		}
		return null;
	}

	private List<FilterCriteria> selectionIncludeCriteria(JsArrayString path, String valueId) {
		if (path == null || valueId == null) {
			return null;
		} else {
			FilterCriteria criteria = JsFilterCriteria.newFilterCriteria();
			criteria.setOperator(FilterData.EQUALS);
			((JsFilterCriteria) criteria).setPathArray(path);
			JsArrayString values = JavaScriptObject.createArray().cast();
			values.push(valueId);
			((JsFilterCriteria) criteria).setValues(values);
			return Collections.singletonList(criteria);
		}
	}

	private void addColumn(String alias, AbstractCellTable<JsCatalogEntry> display, FieldDescriptor field, CatalogDescriptor catalog,
			List<FilterCriteria> includeCriteria, EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, IndexedContentTable table) {

		Header<?> header;
		Column<JsCatalogEntry, ?> column;
		Cell columnCell;
		Cell<String> genericKeyCell;
		String widget = field.getWidget();
		String fieldId = field.getFieldId();
		String fieldName = alias == null ? field.getName() : alias;

		CatalogFormFieldProvider columnCellFactory = fieldFactory.get(widget);
		// assemble column cell
		columnCell = columnCellFactory.createCell(bus, contextServices, contextParameters, formDescriptor, field, CatalogAction.READ);

		try {
			if (ImplicitJoinUtils.isJoinableValueField(field)) {
				// create column title
				if (!field.isMultiple() && !field.isEphemeral()) {
					// TODO support multiple key filters, and ephemeral filters
					header = generateFieldColumnHeader(field, table, display);
				} else {
					header = new TextHeader(fieldName);
				}

				genericKeyCell = (Cell<String>) fieldFactory.get(GENERIC_KEY_CELL_WIDGETID).createCell(bus, contextServices, contextParameters, formDescriptor,
						field, CatalogAction.READ);
				// weird column
				column = new ForeignEntryColumn(new JsArrayAdapterCell(columnCell, genericKeyCell), field, conversionService, includeCriteria);

			} else {
				// create column title
				header = generateFieldColumnHeader(field, table, display);
				// just extract the field value
				column = new FieldColumn(fieldId, columnCell, conversionService, includeCriteria);
			}
			if (column != null) {
				column.setDataStoreName(field.getFieldId());
				column.setSortable(field.isSortable());
				display.addColumn(column, header);
			}
		} catch (Exception e) {
			GWT.log("Attempt to create column failed. catalgog:" + catalog.getName() + " field:" + field.getFieldId() + " widget:" + field.getWidget(), e);
		}
	}

	private Header<?> generateFieldColumnHeader(FieldDescriptor field, final IndexedContentTable table, AbstractCellTable<JsCatalogEntry> display2) {
		FieldColumnHeader header = new FieldColumnHeader(headerDelegate, (JsFieldDescriptor) field);

		header.addValueChangeHandler(new ValueChangeHandler<TableHeaderData>() {
			@Override
			public void onValueChange(ValueChangeEvent<TableHeaderData> event) {
				GWT.log("[content table filter] "+event.getValue().getDescriptor().getName());
				JsFilterData newValue = getNewValue(event.getValue().getDescriptor(), event.getValue().getCriteria(), table.getFilterData());
				table.setValue(newValue);
			}
		});
		//when value changes update criteria value on the header, value changes also fire table to redraw headers after event loop
		table.addValueChangeHandler(header);
		return header;
	}

	protected JsFilterData getNewValue(JsFieldDescriptor descriptor, JsFilterCriteria criteria, JsFilterData old) {
		if (criteria != null & criteria.getPathTokenCount() > 0 ) {
			old.removeFilterByField(criteria.getPath(0));
			old.addFilter(criteria);
		}
		return old;
	}

	@Override
	public void initialize(String[] customColumnIds, AggregateDataTable<JsCatalogEntry> table, EventBus bus, ProcessContextServices services,
                           JsTransactionApplicationContext ctxt, JavaScriptObject properties) {
		String customColumnWidget;
		String customColumnPath;
		Cell<Object> cell;
		Column<DataGroup<JsCatalogEntry>, ?> column;
		CatalogFormFieldProvider columnCellFactory;
		JsFieldDescriptor desc = JavaScriptObject.createObject().cast();
		for (String customColumn : customColumnIds) {
			customColumnWidget = GWTUtils.getAttribute(properties, customColumn + "CustomWidget");
			if (customColumnWidget == null) {
				customColumnWidget = "text";
			}
			customColumnPath = GWTUtils.getAttribute(properties, customColumn + "CustomPath");
			columnCellFactory = fieldFactory.get(customColumnWidget);
			cell = (Cell<Object>) columnCellFactory.createCell(bus, services, ctxt, properties, desc, CatalogAction.READ);
			table.addColumn(new GroupTableColumn(cell, customColumnPath), null);
		}
	}

	public static class GroupTableColumn extends Column<DataGroup<JsCatalogEntry>, Object> {

		String customColumnPath;

		public GroupTableColumn(Cell<Object> cell, String customColumnPath) {
			super(cell);
			this.customColumnPath = customColumnPath;
		}

		@Override
		public Object getValue(DataGroup<JsCatalogEntry> object) {
			String regreso;
			if ("GROUP_PERCENTAGE".equals(customColumnPath)) {
				int pct = (object.getMembers().length() * 100) / object.getTotalPopulation();
				regreso = String.valueOf(pct) + "%";
			} else if ("GROUP_COUNT".equals(customColumnPath)) {
				regreso = String.valueOf(object.getMembers().length());
			} else {
				JsCatalogEntry o = object.getMembers().get(0);
				regreso = GWTUtils.getAttributeFromPath(o, customColumnPath);
			}
			return regreso;
		}

	}

}
