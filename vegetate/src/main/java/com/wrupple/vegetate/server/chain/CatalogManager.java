package com.wrupple.vegetate.server.chain;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.services.PrimaryKeyEncodingService;

public interface CatalogManager extends Catalog {
	
	PrimaryKeyEncodingService getKeyEncodingService();

	CatalogExcecutionContext spawn(CatalogExcecutionContext parent);

	Command getNew();
	
	Command getRead();
	
	Command getWrite();
	
	Command getDelete();

}
