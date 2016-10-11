package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.service.Writers;

@Singleton
public class WritersImpl extends Dictionary implements Writers {

	@Inject
	public WritersImpl(DataWritingCommand defaultCommand) {
		super(defaultCommand);
	}

}
