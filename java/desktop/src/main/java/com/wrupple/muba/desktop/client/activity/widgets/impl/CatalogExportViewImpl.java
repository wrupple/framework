package com.wrupple.muba.desktop.client.activity.widgets.impl;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.widgets.CatalogExportView;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.shared.services.CSVWriter;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;

public class CatalogExportViewImpl extends Composite implements CatalogExportView {

	private static CatalogExportViewImplUiBinder uiBinder = GWT.create(CatalogExportViewImplUiBinder.class);

	interface CatalogExportViewImplUiBinder extends UiBinder<Widget, CatalogExportViewImpl> {
	}

	class ParseAndShow extends DataCallback<CatalogDescriptor> {
		List<JsCatalogEntry> parameter;

		public ParseAndShow(List<JsCatalogEntry> parameter) {
			super();
			this.parameter = parameter;
		}

		@Override
		public void execute() {
			Collection<String> fieldset = result.getFieldNames();
			String csv = presenter.parseEntryFieldsToCSV(fieldset, parameter);
			textArea.setText(csv);
		}

	}

	private StorageManager catalogDescriptor;
	private CSVWriter presenter;
	@UiField
	TextArea textArea;
	@UiField
	Button ok;
	private StateTransition<Void> callback;
	private DesktopManager dm;

	@Inject
	public CatalogExportViewImpl(StorageManager catalogDescriptor,CSVWriter csvutil, CatalogUserInterfaceMessages cc, DesktopManager dm) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter=csvutil;
		this.dm=dm;
		this.catalogDescriptor = catalogDescriptor;
		ok.setText(cc.ok());
	}

	@Override
	public void start(List<JsCatalogEntry> parameter, StateTransition<Void> onDone,EventBus bus) {
		if (parameter == null || parameter.isEmpty()) {

		} else {
			String catalog = parameter.get(0).getCatalog();
			DataCallback<CatalogDescriptor> parseAndShowCSV = new ParseAndShow(parameter);
			catalogDescriptor.loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, parseAndShowCSV);
		}
		this.callback = onDone;
	}


	@UiHandler("ok")
	public void ok(ClickEvent e) {
		callback.execute();
	}


}
