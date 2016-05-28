package com.wrupple.vegetate.server.chain.command;

import java.util.List;

import org.apache.commons.chain.Command;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;


/**
 * 
 * 
 * I knew it was supposed to be like this, and i resisted the simplicity of the idea.
 * 
 * @author japi
 *
 */
public interface CatalogCommand extends Command {

	CatalogEntry create(CatalogEntry uncatalog, CatalogExcecutionContext context)throws Exception;

	List<CatalogEntry> read(FilterData filter, CatalogExcecutionContext context)throws Exception;

	CatalogEntry read(Object targetEntryId, CatalogExcecutionContext context)throws Exception;
	
	CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogExcecutionContext context) throws Exception;

	CatalogEntry update(CatalogEntry originalEntry, CatalogEntry updatedEntry, CatalogExcecutionContext context)throws Exception;

	CatalogEntry delete(Object targetEntryId, CatalogExcecutionContext context)throws Exception;

}
