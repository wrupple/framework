package com.wrupple.vegetate.server.domain;

import java.util.List;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasLocale;

/**
 * @author japi
 *
 */
public interface DomainContext extends Context,CatalogEntry,HasLocale {

	String CATALOG = "Namespace";

	public String getAnonymousPrincipal();

	boolean isRecycleBinEnabled();

	String getCurrencyCode();

	public String getCurrency();

	public boolean isGarbageCollectionEnabled();

	/**
	 * @Named("system.multitenant") Boolean multitenant,
	 * 
	 * @return
	 */
	public boolean isMultitenant();

	public List<String> getGlobalContextExpressions();

	public void setId(long requestedDomain);
	

}
