package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.PointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.UserInteractionWidgetImpl;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.GoogleIndexedPointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.GoogleIndexedPointMapWidget.CatalogKeyMarkerFactory;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.GoogleIndexedPointMapWidget.GeoPositionEntry;
import com.wrupple.muba.worker.shared.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsArrayList;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * handles {@link JsCatalogKey} objects to handle mapping between a latitude
 * longitude field pair, and a foreign key field containing the id and type of
 * the object that location refers to
 * 
 * @author japi
 *
 */
public class GooglePointMapEditorImpl extends
		UserInteractionWidgetImpl<JsCatalogEntry, JsCatalogEntry> implements
		CatalogEditor<JsCatalogEntry> {

	final PointMapWidget<JsCatalogEntry> map;
	private String catalogId;
	private ProcessContextServices services;
	private StorageManager cds;
	/**
	 * id is the Lat-Long field, discriminator is the field containing the id
	 * and type of the object that location refers to
	 */
	private JsArray<GeoPositionEntry> catalogKeys;
	/**
	 * id is the Lat-Long field, discriminator is the field containing the id
	 * and type of the object that location refers to
	 */
	private JsArray<GeoPositionEntry> inferredCatalogKeys;
	private JsCatalogEntry value;
	private CatalogAction mode;

	@Inject
	public GooglePointMapEditorImpl(StorageManager reader, DesktopManager dm,CatalogEditorMap configurationService) {
		super(configurationService);
		this.cds = cds;
		map = new PointMapWidget<JsCatalogEntry>(
				GoogleIndexedPointMapWidget.defaultMapOptions(),
				new CatalogKeyMarkerFactory(reader, null));
		initWidget(map);
	}

	protected void renderValue(CatalogDescriptor catalog, JsCatalogEntry value) {
		setCatalog(catalog);
		this.value = value;
		if (catalogKeys == null) {
			if (inferredCatalogKeys == null) {
				inferredCatalogKeys = JsArray.createArray().cast();
				Collection<FieldDescriptor> fields = catalog
						.getOwnedFieldsValues();
				Set<String> found = new HashSet<String>();
				String fieldId, id;
				GeoPositionEntry key;
				for (FieldDescriptor field : fields) {
					fieldId = field.getFieldId();
					if (GeoPositionEntry.LATITUDE_FIELD.equals(fieldId)
							|| GeoPositionEntry.LONGITUDE_FIELD.equals(fieldId)) {
						if (!found.contains(CatalogEntry.ID_FIELD)) {
							found.add(CatalogEntry.ID_FIELD);
							key = JsCatalogKey.createObject().cast();
							key.setId(CatalogEntry.ID_FIELD);
							inferredCatalogKeys.push(key);
						}
					} else if (fieldId
							.endsWith(GeoPositionEntry.LATITUDE_SUFFIX)) {
						id = fieldId.substring(0, fieldId.length()
								- GeoPositionEntry.LATITUDE_SUFFIX.length());
						if (!found.contains(id)) {
							found.add(id);
							key = JsCatalogKey.createObject().cast();
							key.setId(CatalogEntry.ID_FIELD);
							inferredCatalogKeys.push(key);
						}

					} else if (fieldId
							.endsWith(GeoPositionEntry.LONGITUDE_SUFFIX)) {
						id = fieldId.substring(0, fieldId.length()
								- GeoPositionEntry.LATITUDE_SUFFIX.length());
						if (!found.contains(id)) {
							found.add(id);
							key = JsCatalogKey.createObject().cast();
							key.setId(CatalogEntry.ID_FIELD);
							inferredCatalogKeys.push(key);
						}
					}
				}
			}
		}
		if (catalogKeys == null) {
			renderKeys(inferredCatalogKeys);
		} else {
			renderKeys(catalogKeys);
		}

		fireChangeEvent();
	}

	public void renderKeys(JsArray<GeoPositionEntry> keys) {

		String id, disc;
		double latitude, longitude;
		GeoPositionEntry key;
		JsCatalogKey object;
		for (int i = 0; i < keys.length(); i++) {
			key = keys.get(i).cast();
			id = key.getId();
			disc = key.getStringDiscriminator();
			if (id != null) {
				if (CatalogEntry.ID_FIELD.equals(id)) {
					latitude = GWTUtils.getAttributeAsDouble(value,
							GeoPositionEntry.LATITUDE_FIELD);
					longitude = GWTUtils.getAttributeAsDouble(value,
							GeoPositionEntry.LONGITUDE_FIELD);
				} else {
					latitude = GWTUtils.getAttributeAsDouble(value, id
							+ GeoPositionEntry.LATITUDE_SUFFIX);
					longitude = GWTUtils.getAttributeAsDouble(value, id
							+ GeoPositionEntry.LONGITUDE_SUFFIX);
				}
				key.setLatitude(latitude);
				key.setLongitude(longitude);

				if (disc != null) {
					object = value.getForeignKeyValue(disc);
					if (object != null) {
						key.setName(object.getName());
						key.setImage(object.getImage());
					}
				}
			}

			processInmutableUserConfigurationForPositionKey(key);
		}
		List<GeoPositionEntry> list = JsArrayList.arrayAsList(keys);
		map.setRowData(0, list);
	}

	private void processInmutableUserConfigurationForPositionKey(
			GeoPositionEntry key) {

		String image = GWTUtils.getAttribute(properties, key.getId() + "_"
				+ DesktopPlace.IMAGE_PARAMETER);
		String name = GWTUtils.getAttribute(properties, key.getId() + "_"
				+ CatalogEntry.NAME_FIELD);
		if (name != null) {
			key.setName(name);
		}
		if (image != null) {
			key.setImage(image);
		}
	}

	@Override
	public void setFieldValue(String fieldId, Object v) {
		GWTUtils.setAttributeJava(value, fieldId, v);
		setValue(value);
	}

	@Override
	public void setFieldValue(String fieldId, JSONValue v) {
		JSONObject o = new JSONObject(this.value);
		o.put(fieldId, v);
		setValue(value);
	}

	@Override
	public Object getFieldValue(String field) {
		return GWTUtils.getAttributeAsObject(value, field);
		
	}

	@Override
	public void initialize(String catalog, CatalogAction mode, EventBus bus,
			ProcessContextServices contextServices, JavaScriptObject properties,
			JsTransactionApplicationContext contextParameters) {
		this.catalogId = catalog;
		this.mode = mode;
		this.services = contextServices;
		this.properties = properties;
	}

	/**
	 * @param rawKeys
	 *            lastPosition:driver,departure:id,arrival
	 */
	public void setCatalogKeys(String rawKeys) {
		String[] keyTokens = rawKeys.split(",");
		String id, name;
		int indexOf;
		JsArray<GeoPositionEntry> regreso = JsArray.createArray().cast();
		GeoPositionEntry value;
		for (String rawToken : keyTokens) {
			value = JsCatalogKey.createObject().cast();
			indexOf = rawToken.indexOf(':');
			if (indexOf > 0) {
				id = rawToken.substring(0, indexOf);
				name = rawToken.substring(indexOf, rawToken.length());
			} else {
				name = null;
				id = rawToken;
			}
			value.setId(id);
			value.setStringDiscriminator(name);
			regreso.push(value);
		}
		if (regreso.length() == 0) {
			this.catalogKeys = null;
		} else {
			this.catalogKeys = regreso;
		}

	}

	@Override
	public void setValue(final JsCatalogEntry value) {
		cds.loadCatalogDescriptor(services.getDesktopManager().getCurrentActivityHost(),
				services.getDesktopManager().getCurrentActivityDomain(), catalogId, new DataCallback<CatalogDescriptor>() {

					@Override
					public void execute() {
						renderValue(result, value);
					}
				});
	}

	private void fireChangeEvent() {
		ValueChangeEvent.fire(this, getValue());
	}

	@Override
	public JsCatalogEntry getValue() {
		return value;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JsCatalogEntry> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void reset() {

	}


	@Override
	public CatalogAction getMode() {
		return mode;
	}

	@Override
    protected void onAfterReconfigure(ReconfigurationBroadcastEvent properties2,
                                      ProcessContextServices contextServices, EventBus eventBus,
                                      JsTransactionApplicationContext contextParameters) {
		
	}

	@Override
    protected void onBeforeRecofigure(ReconfigurationBroadcastEvent properties2,
                                      ProcessContextServices contextServices, EventBus eventBus,
                                      JsTransactionApplicationContext contextParameters) {
		
	}
}
