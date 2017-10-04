package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasLocale;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface CatalogNamespace extends CatalogEntry, HasLocale, HasProperties {
	String CATALOG = "Namespace";

	public String getAnonymousPrincipal();

	boolean isRecycleBinEnabled();

	String getCurrencyCode();

	public boolean isGarbageCollectionEnabled();

	public List<String> getGlobalContextExpressions();
}
