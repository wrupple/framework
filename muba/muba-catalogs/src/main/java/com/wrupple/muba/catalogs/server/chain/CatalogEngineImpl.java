package com.wrupple.muba.catalogs.server.chain;

import javax.inject.Inject;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.chain.command.FormatOutput;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.vegetate.server.chain.command.CatalogCommand;

public final class CatalogEngineImpl extends ChainBase implements CatalogEngine {
	
	@Inject
	public CatalogEngineImpl( CatalogRequestInterpret interpret, CatalogCommand commit,
			PublishEvents publishEvents, WriteAuditTrails audit,
			FormatOutput writer) {
		super(new Command[] {interpret,  commit,
				 writer,publishEvents, audit});
	}

}
