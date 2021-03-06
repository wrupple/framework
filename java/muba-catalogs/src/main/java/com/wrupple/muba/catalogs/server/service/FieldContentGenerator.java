package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.server.chain.command.UserCommand;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;

/**
 * 
 * Fills out non-static non-persistent fields of a Catalog Entry.
 * 
 * This fields are usually pointing to some other catalog, or to a generated
 * catalog that calculates values on some obscure process.
 * 
 * @author japi
 * 
 */
public interface FieldContentGenerator {

	/**
	 * @param wrapper the current working catalog entry 
	 * @param descriptor current entry catalog descriptor
	 * @param localField the field this service generates
	 * @param foreignCatalogDescriptor (optional) foreign catalog descriptor in case this field declares one
	 * @param dao (optional) foreign catalog access object in case this field declares a foreign catalog.
	 * @return
	 */
	public List<? extends CatalogEntry> fetchFieldContents(Instrospection accesor, CatalogDescriptor descriptor, FieldDescriptor localField,
                                                           CatalogDescriptor foreignCatalogDescriptor, UserCommand dao) throws Exception;

}
