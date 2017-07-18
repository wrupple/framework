package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import javax.inject.Provider;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.GoogleIndexedPointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.LongitudeCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.SimpleTextCell;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.GoogleLatitudeCellProvider.CreateMap;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.GoogleLatitudeCellProvider.Output;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.GoogleLatitudeCellProvider.Selection;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class GoogleLongitudeCellProvider implements LongitudeCellProvider {

	@Override
	public Cell<? extends Object> createCell(EventBus bus, final ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		if (CatalogAction.READ == mode) {
			return new TextCell();
		} else {
			final String fieldId = d.getFieldId();
			Provider<Process<String, String>> nestedProcessProvider = new Provider<Process<String, String>>() {

				@Override
				public Process<String, String> get() {
					Process<String, String> nestedProcess = new SequentialProcess<String, String>();

					nestedProcess.addState(new CreateMap());
					nestedProcess.addState(new Selection());
					nestedProcess.addState(new Output(contextServices, fieldId,GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_FIELD));

					return nestedProcess;
				}
			};
			SimpleTextCell cell = new SimpleTextCell(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, d.getName());


			return cell;
		}
	}

}
