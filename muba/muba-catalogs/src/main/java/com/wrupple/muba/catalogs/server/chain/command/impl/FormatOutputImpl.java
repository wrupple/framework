package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.catalogs.server.chain.command.FormatOutput;
import com.wrupple.vegetate.domain.CatalogActionRequest;
/**
 * Finds a 
 * 
 * @author japi
 *
 */
@Singleton
public final class FormatOutputImpl extends LookupCommand  implements FormatOutput {
	

	@Inject
	public FormatOutputImpl(CatalogFactory commandDictionatyFactory) {
		super(commandDictionatyFactory);
		//this invokes ContentManagementSystem
		super.setCatalogName(CatalogActionRequest.CATALOG_ID_PARAMETER);
		super.setNameKey(CatalogActionRequest.CATALOG_ID_PARAMETER);
	}
}
