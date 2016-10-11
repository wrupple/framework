package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataDeleteCommand;

public abstract class AbstractDataDeleteCommand implements DataDeleteCommand {

	private final CatalogCreateTransaction create;
	
	
	public AbstractDataDeleteCommand(CatalogCreateTransaction create) {
		super();
		this.create = create;
	}



	@Override
	public void undo(UserContext ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		context.setEntry(context.getResult().getId());
		context.setEntryValue(context.getResult());
		create.execute(context);
	}

}
