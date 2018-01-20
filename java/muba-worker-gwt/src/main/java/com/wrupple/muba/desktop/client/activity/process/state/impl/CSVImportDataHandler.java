package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.process.state.ImportDataHandler;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.event.server.chain.command.impl.ParallelProcess;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation creates the minimal amount of entries possible, updating,
 * or reusing existing entry keys whenever compatible value entries  exists.
 * 
 * @author japi
 * 
 */
public class CSVImportDataHandler implements ImportDataHandler {

	private Provider<CsvRecordImportState> stateProvider;

	private ProcessContextServices context;
	protected final ClientCatalogCacheManager ccm;

	@Inject
	public CSVImportDataHandler(Provider<CsvRecordImportState> stateProvider, ClientCatalogCacheManager ccm) {
		super();
		this.ccm = ccm;
		this.stateProvider = stateProvider;
	}

	@Override
	public void start(final ImportData parameter, StateTransition<List<JsCatalogEntry>> onDone, EventBus bus) {
		ccm.preventInvalidation();
		onDone.hook(new DataCallback<List<JsCatalogEntry>>() {

			@Override
			public void execute() {
				ccm.resumeInvalidation();
			}
		});
		Provider<CsvRecordImportState> wrappedProvider = new Provider<CsvRecordImportState>() {

			@Override
			public CsvRecordImportState get() {
				CsvRecordImportState state = stateProvider.get();
				// parameter.setContextPathIndex(0);
				state.setImportData(parameter);
				state.setContext(context);
				return state;
			}
		};
		// initialize
		ParallelProcess<JsArrayString, JsCatalogEntry> importProcess = new ParallelProcess<JsArrayString, JsCatalogEntry>(wrappedProvider, false,true);

		List<JsArrayString> records = new ArrayList<JsArrayString>(parameter.getCsv().length() - 1);
		for (int i = 1; i < parameter.getCsv().length(); i++) {
			records.add(parameter.getCsv().get(i));
		}
		importProcess.setContext(context);
		// TODO support many to one creation by creating a list of
		// List<FieldImportData> POSTPROCESS and wrapping onDone
		importProcess.start(records, onDone, bus);
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

}