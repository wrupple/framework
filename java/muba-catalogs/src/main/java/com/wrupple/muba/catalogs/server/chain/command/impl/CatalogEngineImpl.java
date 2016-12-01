package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCommand;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;

@Singleton
public final class CatalogEngineImpl extends ChainBase implements CatalogEngine {
	
	@Inject
	public CatalogEngineImpl(/*Set defaults, (like domain language and stuff)*/CatalogCommand commit,
			 WriteAuditTrails audit,PublishEvents /*BPM (& triggers)*/  publishEvents,
			WriteOutput /*CMS*/ writer) {
		super(new Command[] {  commit,
				 writer,publishEvents, audit});
	}

}
