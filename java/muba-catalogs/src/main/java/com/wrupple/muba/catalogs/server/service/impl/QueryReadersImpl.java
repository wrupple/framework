package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataQueryCommand;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import org.apache.commons.chain.Command;

@Singleton
public class QueryReadersImpl extends Dictionary implements QueryReaders {

	@Inject
	public QueryReadersImpl(DataQueryCommand defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public Command getDefault() {
		return super.defaultCommand;
	}
}
