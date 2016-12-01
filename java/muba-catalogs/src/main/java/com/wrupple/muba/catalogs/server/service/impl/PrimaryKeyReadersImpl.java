package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataReadCommand;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;

@Singleton
public class PrimaryKeyReadersImpl extends Dictionary implements PrimaryKeyReaders {

	@Inject
	public PrimaryKeyReadersImpl(DataReadCommand defaultCommand) {
		super(defaultCommand);
	}

}
