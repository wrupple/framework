package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;

public abstract class AbstractDataCreationCommand implements DataCreationCommand {

	private final CatalogDeleteTransaction delete;
	
	public AbstractDataCreationCommand(CatalogDeleteTransaction delete) {
		super();
		this.delete = delete;
	}



	@Override
	public void undo(UserContext ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		context.setEntryValue(context.getResult());
		context.setEntry(context.getResult().getId());
		delete.execute(context);
	}
	

}
