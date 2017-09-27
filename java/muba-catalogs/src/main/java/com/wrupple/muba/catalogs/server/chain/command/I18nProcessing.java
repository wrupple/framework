package com.wrupple.muba.catalogs.server.chain.command;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Command;

import com.wrupple.muba.event.domain.DistributiedLocalizedEntry;

public interface I18nProcessing extends Command {
	/**
	 * Obtains the same result as distributed catalogs with a less scalable but easier to implement and undestand structure
	 */
	public static final String CONSOLIDATED = CatalogDescriptor.CONSOLIDATED;
	/**
	 * entity may look different depending on what server got asked to build
	 * values based on a series of discriminators such as locale. Discriminators are system specific but a FIXME generalized framework is provided
	 */
	public static final String DISTRIBUTED = DistributiedLocalizedEntry.CATALOG;

}
