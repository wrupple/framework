package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.FieldColumnRelation;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.widgets.editors.SequentialListEditor;
import com.wrupple.muba.desktop.client.factory.help.SolverConcensor;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ImportColumnFieldAssignation extends SequentialListEditor implements HumanTask<ImportData, ImportData> {

	static class AdviceBuildingContext {
		Set<String> visitedCatalogs = new HashSet<String>();
		JsArray<JsFieldDescriptor> hierarchy = JavaScriptObject.createArray().cast();

		public void exitHierarchy(JsFieldDescriptor field) {
			JsFieldDescriptor temp;
			for (int i = 0; i < hierarchy.length(); i++) {
				temp = hierarchy.get(i);
				if (temp == field) {
					hierarchy.setLength(i);
					return;
				}
			}
			throw new IllegalArgumentException("field not in hierarchy " + field.getFieldId());
		}

	}

    static class ColumnFieldRelationAssistant implements SolverConcensor {
        private JsArrayString csvColumns;
		private String rootCatalog;
		private StorageManager delegate;
		private DesktopManager dm;

		public ColumnFieldRelationAssistant(StorageManager delegate,DesktopManager dm) {
			this.delegate = delegate;
			this.dm=dm;
		}

		@Override
		public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice) {
			CatalogDescriptor rootDescriptor = delegate.loadFromCache(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), rootCatalog);
			AdviceBuildingContext context = new AdviceBuildingContext();
			context.visitedCatalogs.add(rootCatalog);
			collect(rootCatalog,rootDescriptor, currentState, advice, context);
		}

		private void collect(String catalogId,CatalogDescriptor rootDescriptor, JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice, AdviceBuildingContext context) {
			Collection<FieldDescriptor> rootFields = rootDescriptor.getOwnedFieldsValues();
			String column;
			String neighborName;
			CatalogDescriptor neighbor;
			for (int i = 0; i < csvColumns.length(); i++) {
				column = csvColumns.get(i);
				for (FieldDescriptor field : rootFields) {
					pushIfNotPresent(currentState, advice, column, buildFieldDeclaration(field.getFieldId(), context));
					neighborName = field.getForeignCatalogName();
					if (ImplicitJoinUtils.isJoinableValueField(field) && !context.visitedCatalogs.contains(neighborName)) {
						context.visitedCatalogs.add(neighborName);
						neighbor = delegate.loadFromCache(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), neighborName);
						if (neighbor != null) {
							context.hierarchy.push((JsFieldDescriptor) field);
							collect(neighborName,neighbor, currentState, advice, context);
							context.exitHierarchy((JsFieldDescriptor) field);
						}

					}
				}

			}
		}

		private String buildFieldDeclaration(String fieldId, AdviceBuildingContext context) {
			if (context.hierarchy.length() > 0) {
				StringBuilder builder = new StringBuilder(12 * (context.hierarchy.length() + 1));
				JsFieldDescriptor temp;
				for (int i = 0; i < context.hierarchy.length(); i++) {
					temp = context.hierarchy.get(i);
					builder.append(temp.getFieldId());
					builder.append(".");
				}
				builder.append(fieldId);
				return builder.toString();
			} else {
				return fieldId;
			}
		}

		private void pushIfNotPresent(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice, String column, String fieldDeclaration) {

			// a more efficient way to do this not checking the current state
			// for every clumn every time the user news new advice?
			String aColumn;
			String stateValue;
			for (int i = 0; i < csvColumns.length(); i++) {
				aColumn = csvColumns.get(i);
				if (GWTUtils.hasAttribute(currentState, aColumn)) {
					stateValue = GWTUtils.getAttribute(currentState, aColumn);
					if (stateValue.equals(fieldDeclaration)) {
						return;
					}
				}
			}
			PropertyValueAvisor value;
			value = PropertyValueAvisor.createObject().cast();
			value.setName(column);
			value.setValue(fieldDeclaration);
			advice.push(value);
		}

		@Override
		public void validateValue(String fieldId, Object value, JsArrayString violations) {
			//FIXME verify there is no more that one "key:" per column group
		}

		@Override
		public void setRuntimeParameters(String type, ProcessContextServices ctx) {
		}

		public void setCSVColumns(JsArrayString csvColumns) {
			this.csvColumns = csvColumns;
		}

		public void setRootCatalog(String rootCatalog) {
			this.rootCatalog = rootCatalog;
		}
	}

	@Inject
	public ImportColumnFieldAssignation(CatalogUserInterfaceMessages c, StorageManager delegate, DesktopManager dm) {
		super(c, new ColumnFieldRelationAssistant(delegate, dm));
	}

	@Override
	public void start(final ImportData parameter, final StateTransition<ImportData> onDone, EventBus bus) {
		JsArrayString csvColumns = parameter.getColumnNames();
		((ColumnFieldRelationAssistant) rootAdvisor).setCSVColumns(csvColumns);
		((ColumnFieldRelationAssistant) rootAdvisor).setRootCatalog(parameter.getCatalog());
		addValueChangeHandler(new ValueChangeHandler<JsArrayString>() {
			@Override
			public void onValueChange(ValueChangeEvent<JsArrayString> event) {
				JsArrayString rawValues = event.getValue();
				JsArray<FieldColumnRelation> fieldColumnRelation = JavaScriptObject.createArray().cast();
				String rawValue,  fieldDeclaration,columnDeclaration;
				int splitIndex,columnIndex;
				JsArrayString nestedField;
				boolean discriminative;
				FieldColumnRelation temp;
				fieldColumnRelation.setLength(0);
				for (int i = 0; i < rawValues.length(); i++) {
					rawValue = rawValues.get(i).trim();
					splitIndex = rawValue.indexOf('=');
					columnDeclaration = rawValue.substring(0, splitIndex);
					fieldDeclaration = rawValue.substring(splitIndex + 1);
					
					discriminative=fieldDeclaration.startsWith("key:");
					if(discriminative){
						fieldDeclaration = fieldDeclaration.substring(4);
					}
					nestedField = getNestedField(fieldDeclaration);
					temp=FieldColumnRelation.createObject().cast();
					temp.setDiscriminative(discriminative);
					temp.setPath(nestedField);
					columnIndex = finColumnIndex(columnDeclaration,parameter.getColumnNames());
					temp.setColumn(columnIndex);
					fieldColumnRelation.push(temp);

				}

				parameter.setFieldColumnRelation(fieldColumnRelation);
				onDone.setResultAndFinish(parameter);
			}
		});
	}

	protected int finColumnIndex(String columnDeclaration, JsArrayString columnNames) {
		for(int i = 0 ; i < columnNames.length(); i++){
			if(columnDeclaration.equals(columnNames.get(i))){
				return i;
			}
		}
		throw new IllegalArgumentException("Column "+columnDeclaration+" not found in CSV data");
	}

	private final native JsArrayString getNestedField(String fieldDeclaration) /*-{
		return fieldDeclaration.split(".");
	}-*/;
}
