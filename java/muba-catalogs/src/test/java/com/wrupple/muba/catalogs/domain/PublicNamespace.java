package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public final class PublicNamespace implements CatalogNamespace {
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
	public void setIdAsString(String id) {

	}

	@Override
	public String getIdAsString() {
		return String.valueOf(CatalogEntry.PUBLIC_ID);
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