package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.SimpleTextCell;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
public class CatalogIdAndEntryIdCellProvider implements CatalogFormFieldProvider {

	// Provider<CatalogTypeSelectionProcess> typeSelectionProcessProvider;
	ContentManagementSystem cms;

	@Inject
	public CatalogIdAndEntryIdCellProvider(ContentManagementSystem cms) {
		super();
		this.cms = cms;
	}

	@Override
	public Cell<? extends Object> createCell(EventBus bus,
			final ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		if (CatalogAction.READ == mode) {
			return new TextCell();
		} else {
			final String fieldId = d.getFieldId();
			assert fieldId.equals(JsCatalogEntry.CATALOG_ENTRY_ID_FIELD)||fieldId.equals(JsCatalogEntry.CATALOG_ID_FIELD);
			
			
			Provider<Process<String, String>> nestedProcessProvider= new Provider<Process<String,String>>() {
				
				@Override
				public Process<String, String> get() {
					Process<JsTransactionApplicationContext, JsTransactionApplicationContext> catalogSelectionProcess = cms
							.getContentManager("PersistentCatalogDescriptor")
							.getSelectionProcess(contextServices, false, false);

					Process<String, String> nestedProcess = new SequentialProcess<String, String>();

					nestedProcess.addState(new NullStep());
					nestedProcess.addAll(catalogSelectionProcess);
					nestedProcess.addState(new Selection(nestedProcess,fieldId, contextServices));
					
					return nestedProcess;
				}
			};
			SimpleTextCell cell = new SimpleTextCell(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, d.getName());


			return cell;
		}
	}

	public static class NullStep implements
			State<String, JsTransactionApplicationContext> {

		@Override
		public void start(String parameter,
				StateTransition<JsTransactionApplicationContext> onDone,
				EventBus bus) {
			JsTransactionApplicationContext regreso = JsTransactionApplicationContext
					.createObject().cast();
			onDone.setResultAndFinish(regreso);
		}

	}

	public class Selection
			implements
			State.ContextAware<JsTransactionApplicationContext, JsTransactionApplicationContext> {

		private ProcessContextServices contextServices;
		private Process<String, String> nestedProcess;
		private String whatToOutput;
		private ProcessContextServices parentContext;

		public Selection(Process<String, String> nestedProcess,
				String whatToOutput,ProcessContextServices parentContext) {
			this.nestedProcess = nestedProcess;
			this.whatToOutput = whatToOutput;
			this.parentContext=parentContext;
		}

		@Override
		public void start(JsTransactionApplicationContext parameter,
				StateTransition<JsTransactionApplicationContext> onDone,
				EventBus bus) {
			JsArray<JsCatalogEntry> selectedCatalogArray = parameter
					.getUserOutputAsCatalogEntryArray();
			JsCatalogKey selectedCatalog = selectedCatalogArray.get(0);
			String numericCatalogId = selectedCatalog.getId();
			GWTUtils.setAttribute(parameter, JsCatalogEntry.CATALOG_ID_FIELD,
					numericCatalogId);
			String catalogName = GWTUtils.getAttribute(selectedCatalog,
					"catalogId");
			Process<JsTransactionApplicationContext, JsTransactionApplicationContext> selectionProcess = cms
					.getContentManager(catalogName).getSelectionProcess(contextServices,
							false, false);
			nestedProcess.addAll(selectionProcess);
			nestedProcess.addState(new Output(whatToOutput,parentContext));
			onDone.setResultAndFinish(parameter);

		}

		@Override
		public void setContext(
				ProcessContextServices context) {
			this.contextServices = context;
		}

	}

	public static class Output implements State<JsTransactionApplicationContext, String> {

		private String whatToOutput;
		private final ProcessContextServices contextServices;

		public Output(String whatToOutput,ProcessContextServices contextServices) {
			this.whatToOutput = whatToOutput;
			this.contextServices=contextServices;
		}

		@Override
		public void start(JsTransactionApplicationContext parameter,
				StateTransition<String> onDone, EventBus bus) {
			CatalogEditor<?> eventForm = (CatalogEditor<?>) contextServices.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();

			JsArray<JsCatalogEntry> selectedEntries = parameter
					.getUserOutputAsCatalogEntryArray();
			JsCatalogKey selectedEntry = selectedEntries.get(0);
			String selectedEntryId = selectedEntry.getId();
			String numericCatalogId = GWTUtils.getAttribute(parameter,
					JsCatalogEntry.CATALOG_ID_FIELD);

			if (whatToOutput.equals(JsCatalogEntry.CATALOG_ID_FIELD)) {
				eventForm
						.setFieldValue(JsCatalogEntry.CATALOG_ENTRY_ID_FIELD,selectedEntryId);
				onDone.setResultAndFinish(numericCatalogId);
			} else {
				eventForm.setFieldValue(JsCatalogEntry.CATALOG_ID_FIELD,numericCatalogId);
				onDone.setResultAndFinish(selectedEntryId);
			}

		}


	}

}
