package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.GoogleIndexedPointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.SimpleTextCell;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;

public class GoogleLatitudeCellProvider implements LatitudeCellProvider {

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
					nestedProcess.addState(new Output(contextServices, fieldId, GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_FIELD));

					return nestedProcess;
				}
			};
			SimpleTextCell cell = new SimpleTextCell(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, d.getName());

			return cell;
		}
	}

	public static class CreateMap implements State<String, MapOptions> {

		public CreateMap() {
			super();
		}

		@Override
		public void start(String parameter, StateTransition<MapOptions> onDone, EventBus bus) {
			MapOptions options = MapOptions.newInstance();
			onDone.setResultAndFinish(options);
		}

	}

	public static class Selection implements HumanTask<MapOptions, LatLng> {

		private final SimpleLayoutPanel holder;

		public Selection() {
			super();
			holder = new SimpleLayoutPanel();

		}

		@Override
		public void start(MapOptions parameter, final StateTransition<LatLng> onDone, EventBus bus) {
			MapWidget mapWidget = new MapWidget(parameter);
			holder.setWidget(mapWidget);
			mapWidget.addClickHandler(new ClickMapHandler() {

				@Override
				public void onEvent(ClickMapEvent event) {
					LatLng latLang = event.getMouseEvent().getLatLng();
					onDone.setResultAndFinish(latLang);
				}
			});
		}

		@Override
		public Widget asWidget() {
			return holder;
		}

	}

	public static class Output implements State<LatLng, String> {

		private ProcessContextServices formDescriptor;
		private String fieldId;
		private boolean outputLatitude;

		public Output(ProcessContextServices formDescriptor, String fieldId, String whatToOutput) {
			this.formDescriptor = formDescriptor;
			this.fieldId=fieldId;
			this.outputLatitude=whatToOutput.equals(GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_FIELD);
		}

		@Override
		public void start(LatLng parameter, StateTransition<String> onDone, EventBus bus) {
			CatalogEditor<?> eventForm = (CatalogEditor<?>) formDescriptor.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();
			String outputPairField;
			if (outputLatitude) {
				
				if(fieldId.equals(GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_FIELD)){
					outputPairField=GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_FIELD;
				}else if (fieldId.endsWith(GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_SUFFIX)){
					outputPairField= fieldId.substring(0, fieldId.length()-GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_SUFFIX.length())+GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_SUFFIX;
				}else{
					outputPairField=null;
				}
				
				if(outputPairField!=null){
					eventForm.setFieldValue(outputPairField, String.valueOf(parameter.getLongitude()));
				}
				
				onDone.setResultAndFinish(String.valueOf(parameter.getLatitude()));
			} else {
				if(fieldId.equals(GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_FIELD)){
					outputPairField=GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_FIELD;
				}else if (fieldId.endsWith(GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_SUFFIX)){
					outputPairField= fieldId.substring(0, fieldId.length()-GoogleIndexedPointMapWidget.GeoPositionEntry.LONGITUDE_SUFFIX.length())+GoogleIndexedPointMapWidget.GeoPositionEntry.LATITUDE_SUFFIX;
				}else{
					outputPairField=null;
				}
				
				if(outputPairField!=null){
					eventForm.setFieldValue(outputPairField, String.valueOf(parameter.getLatitude()));
				}
				
				onDone.setResultAndFinish(String.valueOf(parameter.getLongitude()));
			}

		}

	}

}
