package com.wrupple.muba.catalogs.server.chain.command;

import org.apache.commons.chain.Command;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface PublishEvents extends Command {

	/**
	 * 
	 * acts kind of like a compressed version of vegetate's CatalogAction
	 * request
	 * 
	 * @author japi
	 *
	 */
	public interface CatalogBroadcastData {

		Long getDomain();

		String getCatalogId();

		String getAction();

		CatalogEntry getEntry();

		Object getEntryAsSerializable();
		

	}

}
