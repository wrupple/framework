package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataReadCommand;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import org.apache.commons.chain.Command;

@Singleton
public class PrimaryKeyReadersImpl extends Dictionary implements PrimaryKeyReaders {

	@Inject
	public PrimaryKeyReadersImpl(DataReadCommand defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public Command getDefault() {
		return defaultCommand;
	}
}
