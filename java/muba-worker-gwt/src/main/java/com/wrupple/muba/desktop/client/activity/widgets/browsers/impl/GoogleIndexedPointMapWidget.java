package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.overlays.Animation;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.PointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.PointMapWidget.MarkerFactory;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.WruppleIndexedPointMapWidget;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.RemoteStorageUnit;
import com.wrupple.vegetate.client.services.StorageManager;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.List;

public class GoogleIndexedPointMapWidget extends AbstractContentBrowser
		implements WruppleIndexedPointMapWidget {

	@SuppressWarnings("serial")
	public static final class GeoPositionEntry extends JsCatalogEntry {
		public static final String LONGITUDE_FIELD = "longitude";
		public static final String LATITUDE_FIELD = "latitude";
		public static final String LONGITUDE_SUFFIX = "Longitude";
		public static final String LATITUDE_SUFFIX = "Latitude";

		protected GeoPositionEntry() {
			super();
		}

		public native double getLatitude()/*-{
			if (this.latitude == null) {
				return 0;
			}
			return this.latitude;
		}-*/;

		public native double getLongitude()/*-{
			if (this.longitude == null) {
				return 0;
			}
			return this.longitude;
		}-*/;

		public native double setLatitude(double l)/*-{
			this.latitude = l;
		}-*/;

		public native double setLongitude(double l)/*-{
			this.longitude = l;
		}-*/;

	}

	public static class CatalogKeyMarkerFactory implements
			MarkerFactory<JsCatalogEntry> {

		private MapWidget mapWidget;
		private HashMap<String, Marker> map;
		private RemoteStorageUnit reader;
		private DesktopManager dm;

		public CatalogKeyMarkerFactory(StorageManager reader,DesktopManager dm) {
			super();
			this.dm=dm;
			map = new HashMap<String, Marker>();
			this.reader = reader.getRemoteStorageUnit(dm.getCurrentActivityHost());
		}

		@Override
		public Marker getWidget(JsCatalogEntry raw) {
			String id = raw.getId();
			String image = raw.getImage();
			GeoPositionEntry entry = raw.cast();
			LatLng center = LatLng.newInstance(entry.getLatitude(),
					entry.getLongitude());
			MarkerOptions options = MarkerOptions.newInstance();
			options.setPosition(center);
			options.setTitle(raw.getName());
			if (image != null) {
				MarkerImage icon = MarkerImage.newInstance(reader.getImageUri(
						dm.getCurrentActivityDomain(), image, ImageTemplate.SMALL).asString());
				options.setIcon(icon);
			}
			options.setAnimation(Animation.DROP);

			Marker cached = map.get(id);
			Marker marker;
			if (cached == null) {
				marker = Marker.newInstance(options);
				marker.setMap(mapWidget);
			} else {
				marker = cached;
				marker.setOptions(options);
			}

			return marker;
		}

		@Override
		public void setMapWidget(MapWidget mapWidget) {
			this.mapWidget = mapWidget;
		}

	}

	private double smallestLatitude = 0;
	private double largestLatitude = 0;

	private double smallestLongitude = 0;
	private double largestLongitude = 0;
	private final Geolocation geoService;

	@Inject
	public GoogleIndexedPointMapWidget(StorageManager reader,DesktopManager dm,
			Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProviderProvider,
			ContentStyleDelegate styleDelegate,
			CatalogEntryBrowserMap browserMap) {
		super(new PointMapWidget<JsCatalogEntry>(defaultMapOptions(),
				new CatalogKeyMarkerFactory(reader,dm)),
				dinamicDataProviderProvider, keyProviderProvider,
				styleDelegate, browserMap);
		pagingEnabled = false;
		this.geoService = Geolocation.getIfSupported();
	}

	@Override
	public void setRuntimeParams(String newParam, JavaScriptObject properties,
			EventBus bus, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices) {
		setCumulative("true");
		if (geoService != null) {
			geoService
					.getCurrentPosition(new Callback<Position, PositionError>() {
						@Override
						public void onSuccess(Position result) {
							Coordinates coor = result.getCoordinates();

							LatLng center = LatLng.newInstance(
									coor.getLatitude(), coor.getLongitude());
							MarkerOptions options = MarkerOptions.newInstance();
							options.setPosition(center);
							options.setTitle("Location");
							MarkerImage icon = MarkerImage
									.newInstance("/static/img/person.png");
							options.setIcon(icon);
							options.setAnimation(Animation.DROP);
							Marker marker = Marker.newInstance(options);

							PointMapWidget mapWidget = (PointMapWidget) hasData;
							marker.setMap(mapWidget.getMapWidget());
						}

						@Override
						public void onFailure(PositionError reason) {

						}
					});
		}
		super.setRuntimeParams(newParam, properties, bus, contextParameters,
				contextServices);
	}

	public static MapOptions defaultMapOptions() {
		MapOptions optiones = MapOptions.newInstance();
		optiones.setZoom(10);
		return optiones;
	}

	@Override
	public void setRowData(int start, List<? extends JsCatalogEntry> values) {
		super.setRowData(start, values);
		/*
		 * TODO this goes ina layoutDelegate, other methods shoudl become
		 * available
		 */
		GeoPositionEntry entry;
		double thisEntryLatitude = 0;

		double thisEntryLongitude = 0;
		for (JsCatalogEntry value : values) {
			entry = value.cast();
			thisEntryLatitude = entry.getLatitude();
			thisEntryLongitude = entry.getLongitude();
			if (thisEntryLatitude < smallestLatitude) {
				smallestLatitude = thisEntryLatitude;
			} else if (thisEntryLatitude > largestLatitude) {
				largestLatitude = thisEntryLatitude;
			}

			if (thisEntryLongitude < smallestLongitude) {
				smallestLongitude = thisEntryLongitude;
			} else if (thisEntryLongitude > largestLongitude) {
				largestLongitude = thisEntryLongitude;
			}

		}
		PointMapWidget mapWidget = (PointMapWidget) super.hasData;
		if (geoService == null) {
			LatLng smallest = LatLng.newInstance(smallestLatitude,
					smallestLongitude);
			LatLng biggest = LatLng.newInstance(largestLatitude,
					largestLongitude);
			LatLngBounds latLngBounds = LatLngBounds.newInstance(smallest,
					biggest);
			mapWidget.getMapWidget().panToBounds(latLngBounds);
		}

	}

	@Override
	protected void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate) {
		// TODO Auto-generated method stub

	}

}
