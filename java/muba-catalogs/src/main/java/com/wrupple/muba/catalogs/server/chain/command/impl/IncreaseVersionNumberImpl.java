package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.IncreaseVersionNumber;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

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
		Session session = context.getCatalogManager().access().newSession((CatalogEntry) old);
		FieldDescriptor versionField = catalog.getFieldDescriptor(Versioned.FIELD);
		Long version = (Long) context.getCatalogManager().access().getPropertyValue(versionField, (CatalogEntry) old, null, session);
		if(version==null){
			version=0l;
		}else{
			version++;
		}
		context.getCatalogManager().access().setPropertyValue(versionField, (CatalogEntry) updated, version, session);
		return CONTINUE_PROCESSING;
	}

}
