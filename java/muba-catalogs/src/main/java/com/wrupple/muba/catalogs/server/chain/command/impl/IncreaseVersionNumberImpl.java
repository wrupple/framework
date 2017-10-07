package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.IncreaseVersionNumber;
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
		CatalogEntry updated = (CatalogEntry) context.getRequest().getEntryValue();
		Instrospection instrospection = context.getCatalogManager().access().newSession((CatalogEntry) old);
		FieldDescriptor versionField = catalog.getFieldDescriptor(Versioned.FIELD);
		Long version = (Long) context.getCatalogManager().access().getPropertyValue(versionField, (CatalogEntry) old, null, instrospection);
		if(version==null){
			version=0l;
		}else{
			version++;
		}
		context.getCatalogManager().access().setPropertyValue(versionField, (CatalogEntry) updated, version, instrospection);
		return CONTINUE_PROCESSING;
	}

}
