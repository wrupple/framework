package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataQueryCommand;
import com.wrupple.muba.catalogs.server.service.QueryReaders;

@Singleton
public class QueryReadersImpl extends Dictionary implements QueryReaders {

	@Inject
	public QueryReadersImpl(DataQueryCommand defaultCommand) {
		super(defaultCommand);
	}

}
