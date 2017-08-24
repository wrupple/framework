package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.service.EntryCreators;

@Singleton
public class EntryCreatorsImpl extends Dictionary implements EntryCreators {

	@Inject
	public EntryCreatorsImpl(DataCreationCommand defaultCommand) {
		super(defaultCommand);
	}

}
