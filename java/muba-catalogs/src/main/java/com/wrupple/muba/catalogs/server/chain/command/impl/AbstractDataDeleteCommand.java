package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ServiceContext;
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
	public void undo(ServiceContext ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogEntry entry = context.getEntryResult();
		context.getRequest().setEntry(entry.getId());
		context.getRequest().setEntryValue(context.getEntryResult());
		create.execute(context);
	}

}
