package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Provider;

import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;

public abstract class AbstractWritingCommand implements DataWritingCommand {
	private final Provider<CatalogUpdateTransaction> write;
	
	public AbstractWritingCommand(Provider<CatalogUpdateTransaction> write) {
		super();
		this.write = write;
	}


	@Override
	public void undo(UserContext ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		context.setEntryValue(context.getOldValue());
		context.setOldValue(context.getResult());
		write.get().execute(context);
	}

}