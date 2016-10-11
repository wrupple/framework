package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasLocale;
import com.wrupple.muba.bootstrap.domain.HasProperties;

public interface CatalogNamespace extends CatalogEntry, HasLocale, HasProperties {
	String CATALOG = "Namespace";

	public String getAnonymousPrincipal();

	boolean isRecycleBinEnabled();

	String getCurrencyCode();

	public boolean isGarbageCollectionEnabled();

	public List<String> getGlobalContextExpressions();
}
