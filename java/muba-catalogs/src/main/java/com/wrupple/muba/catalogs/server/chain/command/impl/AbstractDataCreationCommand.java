package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ServiceContext;
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
	public void undo(ServiceContext ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		context.setEntryValue(context.getEntryResult());
		CatalogEntry entry = context.getEntryResult();
		context.setEntry(entry.getId());
		delete.execute(context);
	}
	

}
