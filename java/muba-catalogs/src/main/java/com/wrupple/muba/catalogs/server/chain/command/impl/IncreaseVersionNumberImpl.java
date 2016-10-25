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
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;

@Singleton
public class IncreaseVersionNumberImpl implements IncreaseVersionNumber {

	final CatalogEvaluationDelegate accessor;
	
	@Inject
	public IncreaseVersionNumberImpl(CatalogEvaluationDelegate accessor){
		this.accessor=accessor;
		
	}
	
	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogEntry old = (CatalogEntry) context.getOldValue();
		CatalogDescriptor catalog =  context.getCatalogDescriptor();
		CatalogEntry updated = (CatalogEntry) context.getEntryValue();
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
