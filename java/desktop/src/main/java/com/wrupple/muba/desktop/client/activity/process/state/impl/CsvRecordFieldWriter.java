package com.wrupple.muba.desktop.client.activity.process.state.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
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
import com.wrupple.muba.desktop.client.activity.process.state.impl.CsvRecordImportState.FieldImportData;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.logic.impl.GWTFieldConversionStrategyImpl;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.SimpleFilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsArrayList;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

public class CsvRecordFieldWriter implements State<FieldImportData, FieldImportData> {

	class CreateUpdateOrSetForeignEntry extends DataCallback<List<JsCatalogEntry>> {

		final private JsFieldDescriptor parentField;
		final FieldImportData parameter;
		final private StateTransition<FieldImportData> onDone;
		private final EventBus eventBus;
		private final boolean createIfNotFound;
		private boolean attemptUpdate;
		private JsFilterData declaredFields;

		public CreateUpdateOrSetForeignEntry(JsFilterData declaredFields, boolean createIfNotFound, boolean attemptUpdate, EventBus eventBus,
				JsFieldDescriptor field, FieldImportData parameter, StateTransition<FieldImportData> onDone) {
			super();
			this.declaredFields = declaredFields;
			// only if there are more fields than keys
			this.attemptUpdate = attemptUpdate;
			this.parentField = field;
			this.parameter = parameter;
			this.onDone = onDone;
			this.eventBus = eventBus;
			this.createIfNotFound = createIfNotFound;
		}

		@Override
		public void execute() {
			if ((result == null || result.isEmpty())) {
				if (createIfNotFound) {
					// currently only one-to-one relation creation supported
					CsvRecordImportState state = stateProvider.get();
					state.setContext(context);
					ImportData importData = new ImportData(parameter.getImportData().getCsv());
					importData.setCatalog(parentField.getForeignCatalogName());
					importData.setFieldColumnRelation(parameter.getImportData().getFieldColumnRelation());
					JsArray<JsFieldDescriptor> oldContextPath = parameter.getImportData().getContextPath();
					JsArray<JsFieldDescriptor> newContextPath = splice(oldContextPath, parentField);
					importData.setContextPath(newContextPath);

					state.setImportData(importData);
					StateTransition<JsCatalogEntry> nestedCallback = new DataCallback<JsCatalogEntry>() {

						@Override
						public void execute() {
							// AN ATTEMPTH WAS MADE TO CREATE A NEW ENTRY (since
							// none was found) IF THE attempt WAS SUCCESSFUL the
							// id will be set
							convertPutAndEnd(result == null ? null : result.getId(), parentField, parentField.getFieldId(), parameter, onDone);
						}
					};
					state.start(parameter.getRecord(), nestedCallback, eventBus);
				} else {
					onDone.setResultAndFinish(parameter);
				}

			} else {
				/*
				 * SOME MATCHES WERE FOUND!
				 */
				if (attemptUpdate) {
					final CatalogDescriptor descriptor = context.getStorageManager().loadFromCache(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), parentField.getForeignCatalogName());
					JsArray<JsFilterCriteria> declaredValues = declaredFields.getFilterArray();
					List<JsCatalogEntry> discrepancies = null;
					for (JsCatalogEntry entry : result) {
						// match against
						if (delegate.matchAgainstFilters(entry, declaredValues, descriptor)) {
							// ignore
						} else {
							if (discrepancies == null) {
								discrepancies = new ArrayList<JsCatalogEntry>();
							}
							updateEntryFields(entry, declaredValues);
							discrepancies.add(entry);

						}

					}

					if (discrepancies == null || discrepancies.isEmpty()) {
						superCheck(result);
					} else {
						State<JsCatalogEntry, JsCatalogEntry> state = new State<JsCatalogEntry, JsCatalogEntry>() {

							@Override
							public void start(JsCatalogEntry parameter, StateTransition<JsCatalogEntry> onDone, EventBus bus) {
								context.getStorageManager().update(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), parameter.getCatalog(), parameter.getId(), parameter, onDone);
							}
						};
						// if discrepancies exist update CatalogEntry
						ParallelProcess<JsCatalogEntry, JsCatalogEntry> consolidationProcess = new ParallelProcess<JsCatalogEntry, JsCatalogEntry>(state,
								false, false);
						final List<JsCatalogEntry> originalResults = result;
						StateTransition<List<JsCatalogEntry>> consolidationCallback = new DataCallback<List<JsCatalogEntry>>() {

							@Override
							public void execute() {
								superCheck(originalResults);
							}
						};
						consolidationProcess.start(discrepancies, consolidationCallback, eventBus);
					}

				} else {
					superCheck(result);
				}

			}
		}

		private void updateEntryFields(JsCatalogEntry entry, JsArray<JsFilterCriteria> declaredValues) {
			JsFilterCriteria crit;
			for (int i = 0; i < declaredValues.length(); i++) {
				crit = declaredValues.get(i);
				updateEntryField(entry, crit.getPath(0), crit.getValue());
			}
		}

		private native void updateEntryField(JsCatalogEntry entry, String path, Object value) /*-{
			entry[path] = value;
		}-*/;

		protected void superCheck(List<JsCatalogEntry> resultados) {
			if (parentField.isMultiple()) {
				// one to many is supported only if children entities
				// are
				// previously created
				JsArrayString values = JsArrayString.createArray(resultados.size()).cast();
				for (JsCatalogEntry e : resultados) {
					values.push(e.getId());
				}
				putFieldValue(parameter.getEntry(), parentField.getFieldId(), values,false);
				onDone.setResultAndFinish(parameter);
			} else {
				convertPutAndEnd(resultados.get(0).getId(), parentField, parentField.getFieldId(), parameter, onDone);
			}
		}

		private JsArray<JsFieldDescriptor> splice(JsArray<JsFieldDescriptor> oldContextPath, JsFieldDescriptor parentField) {
			JsArray<JsFieldDescriptor> regreso = spliceOrNew(oldContextPath);
			regreso.push(parentField);
			return regreso;
		}

		private native JsArray<JsFieldDescriptor> spliceOrNew(JsArray<JsFieldDescriptor> oldContextPath) /*-{
			if (oldContextPath == null) {
				return [];
			} else {
				return oldContextPath.slice(0);
			}
		}-*/;

	}
	
	class GatherOneToManyKeyValues implements State<JsFilterCriteria,String>{

		final String catalog;
		
		public GatherOneToManyKeyValues(String catalog) {
			super();
			this.catalog = catalog;
		}

		@Override
		public void start(final JsFilterCriteria parameter, final StateTransition<String> onDone, EventBus bus) {
			final StorageManager sm = context.getStorageManager();
			JsFilterData filter= JsFilterData.newFilterData();
			filter.setConstrained(false);
			filter.addFilter(parameter);
			sm.read(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, filter, new DataCallback<List<JsCatalogEntry>>() {

				@Override
				public void execute() {
					if(result==null||result.isEmpty()){
						//create
						String field = parameter.getPath(0);
						JsCatalogEntry newOne = JsCatalogEntry.createCatalogEntry(catalog);
						putFieldValue(newOne, field, parameter.getValuesArray(),true);
						sm.create(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalog, newOne, new DataCallback<JsCatalogEntry>() {

							@Override
							public void execute() {
								onDone.setResultAndFinish(result.getId());
							}
						});
					}else{
						onDone.setResultAndFinish(result.get(0).getId());
					}
				}
			});
		}
		
	}
	

	private final ProcessContextServices context;
	private final FieldConversionStrategy conversionService;
	private final int pathDepth;
	private final Provider<CsvRecordImportState> stateProvider;
	private final CatalogEvaluationDelegate delegate;

	public CsvRecordFieldWriter(Provider<CsvRecordImportState> stateProvider, 
			FieldConversionStrategy conversionService, ProcessContextServices context, int pathDepth, CatalogEvaluationDelegate delegate) {
		this.context = context;
		this.delegate = delegate;
		this.stateProvider = stateProvider;
		this.conversionService = conversionService;
		this.pathDepth = pathDepth;
	}

	@Override
	public void start(final FieldImportData parameter, final StateTransition<FieldImportData> onDone, EventBus bus) {
		JsFieldDescriptor field = parameter.getField();
		final String fieldId = field.getFieldId();
		String rawValue = parameter.getRawValue();
		if (ImplicitJoinUtils.isJoinableValueField(field)) {
			String foreignCatalogId = field.getForeignCatalogName();
			if (rawValue == null) {
				CatalogDescriptor foreignCatalog = context.getStorageManager().loadFromCache(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), foreignCatalogId);
				ImportData importData = parameter.getImportData();
				JsArray<FieldColumnRelation> columnFieldMapping = importData.getFieldColumnRelation();
				JsFilterData declaredFields = JsFilterData.newFilterData();
				JsFilterData declaredKeys = JsFilterData.newFilterData();
				String pathToken;
				JsArrayString path;
				int nextPathLevel = pathDepth + 1;
				String mappedField;
				Object value;
				FieldDescriptor fieldGroupMember;
				FieldColumnRelation mapping;
				boolean hasDiscriminatives = false;
				String rawForeignValue;
				for (int i = 0; i < columnFieldMapping.length(); i++) {

					mapping = columnFieldMapping.get(i);
					path = mapping.getPath();
					// Gather field values to filter and attempt to find an
					// existing
					pathToken = path.get(pathDepth);
					if (pathToken.equals(fieldId)) {
						mappedField = path.get(nextPathLevel);
						fieldGroupMember = foreignCatalog.getFieldDescriptor(mappedField);
						if (fieldGroupMember == null || fieldGroupMember.isMultiple()) {
							// ignore non-declared and multiple fields
							// TODO support finding entries using a multiple
							// field as search criteria
						} else {
							if (!hasDiscriminatives && mapping.isDiscriminative()) {
								hasDiscriminatives = true;
							}
							// check if this column mapping is for a field of
							// the neighbor catalog it must exactly have one
							// more path token besides current (current is
							// foreign key)
							if (nextPathLevel == (path.length() - 1)) {
								/*
								 * TODO support using a foreign key as a search
								 * criteria. Ej: path=product,productLine,name =
								 * "THERMO" this would mean stepping into the
								 * next path token (current is product, next is
								 * productLine (key)) gather all columns of the
								 * group corresponding to product.productLine,
								 * obtain an Id (find or create) and use that as
								 * a search criteria
								 */
								rawForeignValue = parameter.getRecord().get(mapping.getColumn());
								value = conversionService.convertToPersistentDatabaseValue(rawForeignValue, fieldGroupMember);
								if (value == null) {
									// ignore
								} else {
									if (mapping.isDiscriminative()) {
										addCriteria(declaredKeys, mappedField, value);
									}
									// look for an entry with this field value
									addCriteria(declaredFields, mappedField, value);
								}

							}
						}
					}
				}
				
				if(field.isMultiple()){
					/*
					 * THIS IS A ONE TO MANY RELATIONSHIP:
					 */
					if (declaredFields.getFilterArray().length() == 0) {
						// THERE IS NO searching criteria, which means there is
						// no information about the foreign entry, so act as
						// though value was null
						onDone.setResultAndFinish(parameter);
					} else {
						//all members of this column group MUST belong to the same field
						//(there is no way to distinguish which fields belong to which entry otherwise)
						validateGroupMembersBelongToSameField(declaredFields);
						State<JsFilterCriteria, String> state = new GatherOneToManyKeyValues(foreignCatalogId);
						ParallelProcess<JsFilterCriteria, String> gatherKeys = new ParallelProcess<JsFilterCriteria, String>(state , false, false);
						
						List<JsFilterCriteria> declardFieldList=JsArrayList.arrayAsList(declaredFields.getFilterArray());
						StateTransition<List<String>> setKeysIntoFieldAndExit = new DataCallback<List<String>>() {

							@Override
							public void execute() {
								//cannot be null
								JsArrayString regreso = JsArrayString.createArray().cast();
								for(String s: result){
									regreso.push(s);
								}
								putFieldValue(parameter.getEntry(), fieldId, regreso, false);
								onDone.setResultAndFinish(parameter);
							}
						};
						gatherKeys.start(declardFieldList, setKeysIntoFieldAndExit, bus);
					}
					
				}else{
					/*
					 * One to one relationship
					 */
					StorageManager sm = context.getStorageManager();
					CreateUpdateOrSetForeignEntry humanReadableKeyCallback = new CreateUpdateOrSetForeignEntry(declaredFields, true, hasDiscriminatives
							&& (declaredFields.getFilterArray().length() > declaredKeys.getFilterArray().length()), bus, field, parameter, onDone);
					if (hasDiscriminatives) {
						if (declaredKeys.getFilterArray().length() == 0) {
							// THERE IS NO searching criteria, so act as if no
							// entry
							// was found
							humanReadableKeyCallback.setResultAndFinish(null);
						} else {
							GWTUtils.setAttribute(declaredKeys, SimpleFilterableDataProvider.LOCAL_FILTERING, true);
							sm.read(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), foreignCatalogId, declaredKeys, humanReadableKeyCallback);
						}
					} else {
						if (declaredFields.getFilterArray().length() == 0) {
							// THERE IS NO searching criteria, which means there is
							// no information about the foreign entry, so act as
							// though value was null
							// ¿act as if no entry was found? ->
							// humanReadableKeyCallback.setResultAndFinish(null);
							convertPutAndEnd(null, field, fieldId, parameter, onDone);
						} else {
							GWTUtils.setAttribute(declaredFields, SimpleFilterableDataProvider.LOCAL_FILTERING, true);
							sm.read(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), foreignCatalogId, declaredFields, humanReadableKeyCallback);
						}
					}
				}
				
				

			} else {
				convertPutAndEnd(rawValue, field, fieldId, parameter, onDone);
			}
		} else {
			// if value is not key just set it and go
			convertPutAndEnd(rawValue, field, fieldId, parameter, onDone);

		}
	}

	private void validateGroupMembersBelongToSameField(JsFilterData f) {
		JsArray<JsFilterCriteria> filters = f.getFilterArray();
		JsFilterCriteria filter;
		String field=null;
		for(int i = 0 ; i < filters.length(); i++){
			filter = filters.get(i);
			if(field==null){
				field = filter.getPath(0);
			}else{
				if(!field.equals(filter.getPath(0))){
					throw new IllegalArgumentException("In one to many relationship fields, all members of the field's column group must belong to the same field");
				}
			}
		}
	}

	private void addCriteria(FilterData filters, String mappedField, Object value) {
		JsFilterCriteria criteria = JsFilterCriteria.newFilterCriteria();
		criteria.setOperator(FilterData.EQUALS);
		GWTFieldConversionStrategyImpl.setAttribute(criteria, CatalogEntry.ID_FIELD, value);
		pushCriteria(criteria);
		criteria.pushToPath(mappedField);
		filters.addFilter(criteria);

	}

	private native void pushCriteria(JsFilterCriteria criteria) /*-{
		if (criteria.values == null) {
			criteria.values = [];
		}
		criteria.values.push(criteria.id);
		criteria.id = null;
	}-*/;

	private void convertPutAndEnd(String rawValue, FieldDescriptor field, String fieldId, FieldImportData parameter, StateTransition<FieldImportData> onDone) {
		if (field.isMultiple()) {
			if (parameter.getRawValues() == null) {
				GWTUtils.setAttributeJava(parameter.getEntry(), field.getFieldId(), null);
			} else {
				conversionService.convertToPersistentDatabaseValue(parameter.getRawValues(), field, parameter.getEntry());
			}
		} else {
			if (rawValue == null) {
				GWTUtils.setAttributeJava(parameter.getEntry(), field.getFieldId(), null);
			} else {
				conversionService.convertToPersistentDatabaseValue(rawValue, field, parameter.getEntry());
			}
		}
		onDone.setResultAndFinish(parameter);
	}

	private native void putFieldValue(JavaScriptObject temp, String field, Object value, boolean asArray) /*-{
		if(asArray){
			temp[field] = value[0];
		}else{
			temp[field] = value;
		}
	}-*/;

}
