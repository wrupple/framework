package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.safehtml.shared.SafeUri;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.vegetate.domain.CatalogKey;

@SuppressWarnings("serial")
public class JsCatalogKey extends JavaScriptObject implements CatalogKey {

	protected JsCatalogKey() {
	}

	@Override
	public final String getIdAsString() {
		return getId();
	}

	public final static Long parseKeyField(String l) {
		return Long.parseLong(l);
	}

	@Override
	public final void setIdAsString(String id) {
		setId(id);
	}

	@Override
	public final native String getCatalog() /*-{
		return this.catalog;
	}-*/;

	@Override
	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName()/*-{
		return this.name;
	}-*/;
	public final native String getImage()/*-{
		return this.image;
	}-*/;
	
	public final native void setImage(String image)/*-{
		this.image=image;
	}-*/;
	public final native String getStaticImageUrl() /*-{
		return this.staticImageUrl;
	}-*/;

	public final native SafeUri getStaticImageUri() /*-{
		return this.staticImageUri;
	}-*/;

	public final native void setStaticImageUrl(String uri) /*-{
		this.staticImageUrl = uri;
	}-*/;

	public final native void setStaticImageUri(SafeUri uri) /*-{
		this.staticImageUri = uri;
	}-*/;

	public final native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public final native void setCatalog(String catalog) /*-{
		this.catalog = catalog;
	}-*/;

	public final native void setStringDiscriminator(String discriminator) /*-{
		this.discriminator = discriminator;
	}-*/;

	public final native String getStringDiscriminator() /*-{
		return this.discriminator;
	}-*/;

	public final native void setId(String id) /*-{
		this.id = id;
	}-*/;

	public final JsCatalogKey getForeignKeyValue(String fieldid) {
		JavaScriptObject o = GWTUtils.getAttributeAsJavaScriptObject(this,
				fieldid + FOREIGN_KEY);
		if (o == null) {
			return null;
		} else {
			return o.cast();
		}
	}

	public final List<String> getProperties() {
		JsArrayString properties = getPropertiesArray();
		if (properties == null) {
			return null;
		} else {
			return GWTUtils.asStringList(properties);
		}
	}

	public final JavaScriptObject getPropertiesObject() {
		return GWTUtils.getPropertiesObject(getPropertiesArray());
	}

	public final native JsArrayString getPropertiesArray() /*-{
		return this.properties;
	}-*/;

	public final native void setPropertiesArray(JsArrayString a) /*-{
		this.properties = a;
	}-*/;

	public final native void addProperty(String key, String value) /*-{
		if (this.properties == null) {
			this.properties = [];
		}
		this.properties.push(key + "=" + value);
	}-*/;

	/**
	 * 
	 * inserts new properties with a higher priority than those currently
	 * contained
	 * 
	 * @param newProperties
	 */
	public final native void insertProperties(JsArrayString newProperties) /*-{
		if (newProperties == null) {
			return;
		}
		if (this.properties == null) {
			this.properties = [];
		}
		var p;
		for (var i = 0; i < newProperties.length; i++) {
			p = newProperties[i];
			this.properties.push(p);
		}

	}-*/;

}
