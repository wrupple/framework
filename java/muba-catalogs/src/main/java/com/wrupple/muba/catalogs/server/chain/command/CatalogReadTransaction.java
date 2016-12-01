package com.wrupple.muba.catalogs.server.chain.command;

import org.apache.commons.chain.Command;

public interface CatalogReadTransaction extends Command {
	/**
	 * forces full implicit join 
	 */
	final String READ_GRAPH = "catalog.result.full_join";
}
