package com.wrupple.muba.desktop.client.activity.process.state.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.ParallelProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.FieldColumnRelation;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.services.logic.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.SimpleFilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

public class CsvRecordImportState implements State.ContextAware<JsArrayString, JsCatalogEntry> {

	public static class FieldImportData {
		final JsCatalogEntry entry;
		final JsArrayString record;
		final ImportData importData;
		final JsFieldDescriptor field;
		final JsCatalogDescriptor descriptor;
		final String rawValue;
		private final JsArrayString rawValues;
		final FieldColumnRelation columnFieldMaping;

		public FieldImportData(String rawValue, JsCatalogEntry entry, JsArrayString record, ImportData importData, JsFieldDescriptor field,
				JsCatalogDescriptor descriptor, FieldColumnRelation columnFieldMaping) {
			this(rawValue, entry, record, importData, field, descriptor, null, columnFieldMaping);
		}

		public FieldImportData(String rawValue, JsCatalogEntry entry, JsArrayString record, ImportData importData, JsFieldDescriptor field,
				JsCatalogDescriptor descriptor, JsArrayString rawValues, FieldColumnRelation columnFieldMaping) {
			this.rawValue = rawValue;
			this.entry = entry;
			this.record = record;
			this.importData = importData;
			this.columnFieldMaping = columnFieldMaping;
			this.field = field;
			this.rawValues = rawValues;
			this.descriptor = descriptor;
		}

		public FieldColumnRelation getColumnFieldMaping() {
			return columnFieldMaping;
		}

		public JsCatalogEntry getEntry() {
			return entry;
		}

		public JsArrayString getRecord() {
			return record;
		}

		public ImportData getImportData() {
			return importData;
		}

		public JsFieldDescriptor getField() {
			return field;
		}

		public JsCatalogDescriptor getDescriptor() {
			return descriptor;
		}

		public String getRawValue() {
			return rawValue;
		}

		public JsArrayString getRawValues() {
			return rawValues;
		}

	}

	private final FieldConversionStrategy conversionService;
	private ProcessContextServices context;
	private ImportData importData;
	private Provider<CsvRecordImportState> stateProvider;
	private CatalogEvaluationDelegate delegate;

	@Inject
	public CsvRecordImportState(CatalogEvaluationDelegate delegate, FieldConversionStrategy conversionService, 
			Provider<CsvRecordImportState> stateProvider) {
		this.delegate = delegate;
		this.conversionService = conversionService;
		this.stateProvider = stateProvider;
	}

	@Override
	public void start(JsArrayString parameter, final StateTransition<JsCatalogEntry> onDone, EventBus bus) {
		final String catalog = importData.getCatalog();
		final JsCatalogDescriptor descriptor = (JsCatalogDescriptor) context.getStorageManager().loadFromCache(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog);
		final JsArray<JsFieldDescriptor> fields = descriptor.getFieldArray();

		final JsCatalogEntry createdEntry = JsCatalogEntry.createCatalogEntry(catalog);
		JsArray<JsFieldDescriptor> contextPath = importData.getContextPath();
		int contextPathIndex = importData.getContextPathIndex();
		State<FieldImportData, FieldImportData> fieldWriter = new CsvRecordFieldWriter(this.stateProvider, conversionService,
				context, contextPathIndex, delegate);
		ParallelProcess<FieldImportData, FieldImportData> fieldWritingProcess = new ParallelProcess<FieldImportData, FieldImportData>(fieldWriter, false,false);

		List<FieldImportData> fieldsToProcess = new ArrayList<CsvRecordImportState.FieldImportData>(importData.getColumnNames().length());
		JsFieldDescriptor field;
		FieldImportData fieldData;
		JsArray<FieldColumnRelation> columnFieldMaping = importData.getFieldColumnRelation();
		String rawValue, fieldId;
		JsArrayString fieldpath;
		JsArrayString rawValues;
		for (int i = 0; i < fields.length(); i++) {
			field = fields.get(i);
			fieldId = field.getFieldId();

			if (field.isMultiple()) {
				rawValue = null;
				rawValues = null;
				FieldColumnRelation mappedRelation = null;
				for (int j = 0; j < columnFieldMaping.length(); j++) {
					mappedRelation = columnFieldMaping.get(j);
					fieldpath = mappedRelation.getPath();

					if (fieldMappingMatchesCurrentField(fieldpath, contextPath, fieldId)) {
						// this field matches a non empty column
						rawValue = parameter.get(j);
						if(rawValue!=null){
							if (rawValues == null) {
								rawValues = JavaScriptObject.createArray().cast();
							}
							// in multiple fields, it is allowed for more than one
							// column to match and all values are stored
							rawValues.push(rawValue);
						}
					}
				}

				if (rawValue != null || ImplicitJoinUtils.isJoinableValueField(field)) {
					fieldData = new FieldImportData(rawValue, createdEntry, parameter, importData, field, descriptor, rawValues, mappedRelation);
					fieldsToProcess.add(fieldData);
				}
			} else {
				fieldData = processRecord(columnFieldMaping, field, parameter, contextPath, createdEntry, descriptor);
				if (fieldData != null) {
					fieldsToProcess.add(fieldData);
				}

			}
		}

		if (fieldsToProcess.isEmpty()) {
			onDone.setResultAndFinish(createdEntry);
		} else {
			StateTransition<List<FieldImportData>> callback = new DataCallback<List<FieldImportData>>() {
				//CALLED WHEN ALL FIELDS ARE DONE PROCESSING	
				@Override
				public void execute() {
					// build search criteria
					JsFilterData filter = JsFilterData.newFilterData();
					JsFilterCriteria criteria;
					String fieldId;
					Object value;
					boolean keyFieldsOnly=false;
					FieldColumnRelation mapping;
					for (FieldImportData i : result) {
						fieldId = i.getField().getFieldId();
						value = GWTUtils.getAttributeAsObject(createdEntry, fieldId);
						if (value != null) {
							criteria = JsFilterCriteria.newFilterCriteria();
							criteria.setOperator(FilterData.EQUALS);
							criteria.pushToPath(fieldId);
							criteria.setValue(value);
							mapping = i.getColumnFieldMaping();
							if (mapping != null  &&mapping.isDiscriminative()) {
								if(!keyFieldsOnly){
									keyFieldsOnly=true;
									filter = JsFilterData.newFilterData();
								}
							}
							if(keyFieldsOnly){
								if(mapping != null  &&mapping.isDiscriminative()){
									filter.addFilter(criteria);
								}
							}else{
								filter.addFilter(criteria);
							}
							
						}
					}
					if (filter.getFilterArray().length() > 0 /*
															 * TODO ||
															 * always-create
															 * -entries option
															 * (no pre-existing
															 * check)
															 */) {
						// check for an entry with the same user assigned key,
						// or
						// with all fields equal
						GWTUtils.setAttribute(filter, SimpleFilterableDataProvider.LOCAL_FILTERING, true);
						context.getStorageManager().read(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, filter, new DataCallback<List<JsCatalogEntry>>() {

							@Override
							public void execute() {

								if (result == null || result.isEmpty()) {

									// create (vegetate throwttle should prevent
									// duplicates)
									context.getStorageManager().create(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, createdEntry, onDone);

								} else {
									// if exists: update
									State<JsCatalogEntry, JsCatalogEntry> update = new State<JsCatalogEntry, JsCatalogEntry>() {

										@Override
										public void start(JsCatalogEntry parameter, StateTransition<JsCatalogEntry> onDone, EventBus bus) {
											if(copyAllPropertiesNoOverwrite(createdEntry,parameter )){
												context.getStorageManager().update(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, parameter.getId(), createdEntry, onDone);
											}else{
												onDone.setResultAndFinish(parameter);
											}
										}
									};
									ParallelProcess<JsCatalogEntry, JsCatalogEntry> consolidatingProcess = new ParallelProcess<JsCatalogEntry, JsCatalogEntry>(
											update, false,false);
									StateTransition<List<JsCatalogEntry>> consolidatingCallback = new DataCallback<List<JsCatalogEntry>>() {

										@Override
										public void execute() {
											onDone.setResultAndFinish(result.get(0));

										}
									};
									consolidatingProcess.start(result, consolidatingCallback, context.getEventBus());
								}

							}

						});

					} else {

						// create
						FieldDescriptor field;
						for (int i = 0; i < fields.length(); i++) {
							field = fields.get(i);
							if (GWTUtils.hasAttribute(createdEntry, field.getFieldId())) {
								if (!field.getFieldId().equals(descriptor.getKeyField())) {
									context.getStorageManager().create(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, createdEntry, onDone);
									break;
								}
							}
						}
						onDone.setResultAndFinish(createdEntry);

					}
				}
			};
			fieldWritingProcess.start(fieldsToProcess, callback, bus);
		}
	}

	native boolean copyAllPropertiesNoOverwrite(JavaScriptObject target, JavaScriptObject source) /*-{
		var sourceValue;
		var oldValue;
		var changed = false;
		for ( var k in source) {
			sourceValue = source[k];
			if (sourceValue != null) {
				oldValue = target[k];
				target[k] = sourceValue;
				if(oldValue!=target[k]){
					changed=true;
				}
			}
		}
		return changed;
	}-*/;

	// return null when looking for product key, but a product.name or
	// product.model value is present
	private FieldImportData processRecord(JsArray<FieldColumnRelation> columnFieldMaping, JsFieldDescriptor field, JsArrayString parameter,
			JsArray<JsFieldDescriptor> contextPath, JsCatalogEntry createdEntry, JsCatalogDescriptor descriptor) {
		JsArrayString fieldpath;
		String fieldId = field.getFieldId();
		int columnIndex = -1;
		FieldColumnRelation mappedRelation;
		String rawValue;
		for (int i = 0; i < columnFieldMaping.length(); i++) {
			mappedRelation = columnFieldMaping.get(i);
			fieldpath = mappedRelation.getPath();
			if (fieldMappingMatchesCurrentField(fieldpath, contextPath, fieldId)) {
				columnIndex = mappedRelation.getColumn();
				if (columnIndex < parameter.length()) {
					rawValue = parameter.get(columnIndex);
					if (rawValue == null) {
						if (ImplicitJoinUtils.isJoinableValueField(field)) {
							return new FieldImportData(null, createdEntry, parameter, importData, field, descriptor, mappedRelation);
						} else {
							return null;
						}

					}
					return new FieldImportData(rawValue, createdEntry, parameter, importData, field, descriptor, mappedRelation);
				} else {
					GWT.log("csv column out of bounds for current row");
					break;
				}
			}
		}
		if (ImplicitJoinUtils.isJoinableValueField(field)) {
			return new FieldImportData(null, createdEntry, parameter, importData, field, descriptor, null);
		}
		return null;
	}

	/**
	 * checks each element in fieldMapping to each element in fields
	 * 
	 * @param fieldMapping
	 * @param trailingFields
	 * @param topMostField
	 * @return TRUE if this field maps to a column in the csv data
	 */
	public static boolean fieldMappingMatchesCurrentField(JsArrayString fieldMapping, JsArray<JsFieldDescriptor> trailingFields, String topMostField) {
		if (fieldMapping != null && fieldMapping.length() > 0) {
			int mappingDepth = fieldMapping.length();
			int currentDepth = 1 + (trailingFields == null ? 0 : trailingFields.length());
			if (currentDepth == mappingDepth) {
				String token;
				String compareToken;
				int i = 0;
				for (; i < fieldMapping.length() && trailingFields != null && i < trailingFields.length(); i++) {
					token = fieldMapping.get(i);
					compareToken = trailingFields.get(i).getFieldId();
					if (!token.equals(compareToken)) {
						return false;
					}
				}

				return topMostField.equals(fieldMapping.get(i));

			}
		}
		return false;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

	public void setImportData(ImportData importData) {
		this.importData = importData;
	}

}
