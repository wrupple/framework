package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.process.state.ImportDataInputTask;
import com.wrupple.muba.desktop.client.activity.widgets.ImportView;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.CSVParser;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.CatalogDescriptor;

import java.util.List;

public class ImportCSVDataInputTask implements ImportDataInputTask {
	
	class ReadValuesAndExit implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String csv = view.getCsv();
			final ImportData output = parser.parse(csv);
			output.setCatalog( parameter.getSelectedType());
			
			StateTransition<CatalogDescriptor> onDone = new DataCallback<CatalogDescriptor>() {
				@Override
				public void execute() {
					callback.setResultAndFinish(output);
				}
			};
			delegate.loadGraphDescription(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), output.getCatalog(), onDone);
		}
	}

	final ImportView view;
	final CSVParser parser;
	private StateTransition<ImportData> callback;
	private CatalogProcessDescriptor parameter;
	private final StorageManager delegate;
	private DesktopManager dm;
	
	@Inject
	public ImportCSVDataInputTask(ImportView view, CSVParser parser,DesktopManager dm, StorageManager sm) {
		super();
		this.delegate= sm;
		this.dm =dm;
		this.view = view;
		this.parser = parser;
	}


	@Override
	public void start(CatalogProcessDescriptor parameter,
			StateTransition<ImportData> onDone, EventBus bus) {
		this.parameter=parameter;
		this.callback=onDone;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	public void setAction(List<? extends HasClickHandlers> a) {
		List<Image> actions = (List<Image>) a;
		actions.get(0).addClickHandler(new ReadValuesAndExit());
		view.addActions(actions);
	}

	
}
