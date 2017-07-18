package com.wrupple.muba.bootstrap.domain;

import java.util.List;

import javax.transaction.UserTransaction;

import com.wrupple.muba.bootstrap.server.chain.command.UserCommand;

/**
 * Allows implementations to support ReSTful transaction through a undo history
 * log
 * 
 * @author japi
 *
 */
public interface TransactionHistory extends UserTransaction {

	<T extends CatalogEntry> void didRead(ServiceContext context, List<T> r, UserCommand dao);

	public <T extends CatalogEntry> void didRead(ServiceContext context, T r, UserCommand dao);

	public <T extends CatalogEntry> void didCreate(ServiceContext context, CatalogEntry regreso,
			UserCommand createDao);

	public <T extends CatalogEntry> void didUpdate(ServiceContext context, T original, T outDatedEntry,
			UserCommand dao);
	
	public <T extends CatalogEntry> void didDelete(ServiceContext context, T r, UserCommand dao);
	
	public void didMetadataRead(ContractDescriptor regreso) ;

}
