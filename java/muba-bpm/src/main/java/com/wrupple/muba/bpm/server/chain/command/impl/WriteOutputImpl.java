package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;
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
