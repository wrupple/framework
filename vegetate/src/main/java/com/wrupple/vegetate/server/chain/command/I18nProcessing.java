package com.wrupple.vegetate.server.chain.command;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.domain.HasLocale;

public interface I18nProcessing extends CatalogCommand {
	/**
	 * Obtains the same result as distributed catalogs with a less scalable but easier to implement and undestand structure
	 */
	public static final String CONSOLIDATED = "MONOLITIC";
	/**
	 * entity may look different depending on what server got asked to build
	 * values based on a series of discriminators such as locale. Discriminators are system specific but a FIXME generalized framework is provided
	 */
	public static final String DISTRIBUTED = DistributiedLocalizedEntry.CATALOG;

	public interface DistributiedLocalizedEntry extends CatalogEntry, HasLocale,HasCatalogId,HasEntryId {

		
		String CATALOG = "DistributedCatalog";

		String getLocalizedFieldValue(String fieldId);
		
		Long getCatalogId();
		
		Long getCatalogEntryId();

		List<String> getProperties();

	}

}
