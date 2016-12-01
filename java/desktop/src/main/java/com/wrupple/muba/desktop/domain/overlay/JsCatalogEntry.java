package com.wrupple.muba.desktop.domain.overlay;

import java.util.List;

import com.google.gwt.core.client.JsArrayNumber;
import com.wrupple.vegetate.domain.CatalogEntry;

public class JsCatalogEntry extends JsEntity implements CatalogEntry{

	private static final long serialVersionUID = 1L;

	protected JsCatalogEntry() {
		super();
	}

	

	public final static JsCatalogEntry createCatalogEntry(String catalog) {
		JsCatalogEntry regreso = JsCatalogEntry.createObject().cast();
		regreso.setCatalog(catalog);
		return regreso;
	}

	@Override
	public final Long getDomain() {
		// CLIENT SIDE NO DOMAINS APPLY
		return null;
	}

	@Override
	public final void setDomain(Long domain) {

	}

	@Override
	public final native boolean isAnonymouslyVisible() /*-{
		return this.anonymouslyVisible;
	}-*/;

	@Override
	public final native void setAnonymouslyVisible(boolean p) /*-{
		this.anonymouslyVisible = p;
	}-*/;
	
	


	public final static List<Long> convertLongArray(JsArrayNumber numberArr) {
		throw new IllegalArgumentException();
	}
		
}
