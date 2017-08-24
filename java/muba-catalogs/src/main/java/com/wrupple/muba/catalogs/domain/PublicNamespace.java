package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;

public final class PublicNamespace implements CatalogNamespace {
	private static final long serialVersionUID = -6580849358985409720L;

	@Override
	public void setProperties(List<String> properties) {

	}

	@Override
	public List<String> getProperties() {
		return null;
	}

	@Override
	public String getLocale() {
		return System.getProperty("user.language", "en");
	}

	@Override
	public Object getId() {
		return CatalogEntry.PUBLIC_ID;
	}

	@Override
	public String getCatalogType() {
		return CATALOG;
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public String getName() {
		return CatalogEntry.PUBLIC;
	}

	@Override
	public String getImage() {
		return null;
	}

	@Override
	public void setDomain(Long domain) {

	}

	@Override
	public void setAnonymouslyVisible(boolean p) {

	}

	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}

	@Override
	public Object getDomain() {
		return getId();
	}

	@Override
	public boolean isRecycleBinEnabled() {
		return true;
	}

	@Override
	public boolean isGarbageCollectionEnabled() {
		return true;
	}

	@Override
	public List<String> getGlobalContextExpressions() {
		return null;
	}

	@Override
	public String getCurrencyCode() {
		return "USD";
	}

	@Override
	public String getAnonymousPrincipal() {
		return "anonymous";
	}
}