package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
/**
 * Finds a 
 * 
 * @author japi
 *
 */
@Singleton
public final class WriteOutputImpl extends LookupCommand  implements WriteOutput {
	
	

	@Inject
	public WriteOutputImpl(CatalogFactory commandDictionatyFactory) {
		super(commandDictionatyFactory);
		//this invokes FormatDictionary
		super.setCatalogName(CatalogActionRequest.CATALOG_FIELD);
		super.setNameKey(CatalogActionRequest.CATALOG_FIELD);
	}
}
