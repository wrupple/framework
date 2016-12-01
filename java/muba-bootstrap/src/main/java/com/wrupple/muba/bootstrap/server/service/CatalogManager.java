package com.wrupple.muba.bootstrap.server.service;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

/**
 * 
 * 
 * @author japi
 *
 */
public interface CatalogManager extends Catalog {
	


	Command getNew();
	
	Command getRead();
	
	Command getWrite();
	
	Command getDelete();

	

	

}
