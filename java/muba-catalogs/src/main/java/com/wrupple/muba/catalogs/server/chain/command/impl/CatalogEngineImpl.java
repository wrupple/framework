package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCommand;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents;
import com.wrupple.muba.catalogs.server.chain.command.ValidateUserData;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;

@Singleton
public final class CatalogEngineImpl extends ChainBase implements CatalogEngine {
	
	@Inject
	public CatalogEngineImpl( ValidateUserData validate, CatalogCommand commit,
			 WriteAuditTrails audit,PublishEvents /*BPM (& triggers)*/  publishEvents,
			WriteOutput /*CMS*/ writer) {
		super(new Command[] { validate, commit,
				 writer,publishEvents, audit});
	}

}
