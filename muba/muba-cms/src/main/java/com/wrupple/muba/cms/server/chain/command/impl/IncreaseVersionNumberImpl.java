package com.wrupple.muba.cms.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.cms.server.chain.command.IncreaseVersionNumber;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.Versioned;

public class IncreaseVersionNumberImpl implements IncreaseVersionNumber {

	final CatalogPropertyAccesor accessor;
	
	@Inject
	public IncreaseVersionNumberImpl(CatalogPropertyAccesor accessor){
		this.accessor=accessor;
		
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		CatalogEntry old = (CatalogEntry) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
		CatalogEntry updated = (CatalogEntry) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		Session session = accessor.newSession((CatalogEntry) old);
		FieldDescriptor versionField = catalog.getFieldDescriptor(Versioned.FIELD);
		Long version = (Long) accessor.getPropertyValue(catalog, versionField, (CatalogEntry) old, null, session);
		if(version==null){
			version=0l;
		}else{
			version++;
		}
		accessor.setPropertyValue(catalog, versionField, (CatalogEntry) updated, version, session);
		return CONTINUE_PROCESSING;
	}

}
