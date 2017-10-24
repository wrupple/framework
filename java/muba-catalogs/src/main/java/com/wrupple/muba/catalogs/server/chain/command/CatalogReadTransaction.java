package com.wrupple.muba.catalogs.server.chain.command;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Instrospection;
import org.apache.commons.chain.Command;

public interface CatalogReadTransaction extends Command {

    CatalogEntry readVanityId(String catalogid, CatalogDescriptor metadataDescriptor, CatalogActionContext context, CatalogResultCache metadataCache, Instrospection introspection) throws Exception;
}
