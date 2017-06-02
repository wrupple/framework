package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.IncreaseVersionNumber;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;

@Singleton
public class IncreaseVersionNumberImpl implements IncreaseVersionNumber {

	
	@Inject
	public IncreaseVersionNumberImpl(){
		
	}
	
	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogEntry old = (CatalogEntry) context.getOldValue();
		CatalogDescriptor catalog =  context.getCatalogDescriptor();
		CatalogEntry updated = (CatalogEntry) context.getEntryValue();
		Session session = context.getCatalogManager().newSession((CatalogEntry) old);
		FieldDescriptor versionField = catalog.getFieldDescriptor(Versioned.FIELD);
		Long version = (Long) context.getCatalogManager().getPropertyValue(versionField, (CatalogEntry) old, null, session);
		if(version==null){
			version=0l;
		}else{
			version++;
		}
		context.getCatalogManager().setPropertyValue(versionField, (CatalogEntry) updated, version, session);
		return CONTINUE_PROCESSING;
	}

}
