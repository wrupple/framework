package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;

class NestedProcessDesktopManager implements DesktopManager {
	DesktopManager delegate;

	public NestedProcessDesktopManager(DesktopManager delegate) {
		this.delegate = delegate;
	}

	@Override
	public void endUserSession() {
		delegate.endUserSession();
	}

	@Override
	public DesktopPlace getDefaultPlace() {
		return delegate.getDefaultPlace();
	}

	@Override
	public <T extends JavaScriptObject> T getUser() {
		return delegate.getUser();
	}

	@Override
	public void setUserObject(JavaScriptObject userData) {
		delegate.setUserObject(userData);
	}

	@Override
	public JavaScriptObject getApplicationItem(DesktopPlace desktopPlace) {
		return delegate.getApplicationItem(desktopPlace);
	}

	@Override
	public void putPlaceParameter(String filterDataParameter, String unencodedString) {

	}
	
	@Override
	public void updatePlace(DesktopPlace currentPlace) {
		
	}

	@Override
	public boolean isLandscape() {
		return delegate.isLandscape();
	}

	@Override
	public boolean isDesktopyConfigured() {
		return delegate.isDesktopyConfigured();
	}

	@Override
	public String getCurrentActivityDomain() {
		return delegate.getCurrentActivityDomain();
	}

	@Override
	public String getCurrentActivityHost() {
		return delegate.getCurrentActivityHost();
	}

	@Override
	public JavaScriptObject getCurrentApplicationItem() {
		return delegate.getCurrentApplicationItem();
	}

	@Override
	public boolean isSSL() {
		return delegate.isSSL();
	}

	@Override
	public ApplicationItem getApplicationItem(String itemId) {
		return delegate.getApplicationItem(itemId);
	}


}