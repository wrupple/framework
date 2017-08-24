package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.impl.Dictionary;
import com.wrupple.muba.catalogs.server.chain.command.DataDeleteCommand;
import com.wrupple.muba.catalogs.server.service.Deleters;

@Singleton
public class DeletersImpl extends Dictionary implements Deleters{

	@Inject
	public DeletersImpl(DataDeleteCommand defaultCommand) {
		super(defaultCommand);
	}

}
