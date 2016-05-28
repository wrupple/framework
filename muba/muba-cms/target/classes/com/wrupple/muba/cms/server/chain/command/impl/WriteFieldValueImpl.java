package com.wrupple.muba.cms.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.cms.server.chain.command.WriteFieldValue;
import com.wrupple.muba.cms.server.domain.ContentContext;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public class WriteFieldValueImpl implements WriteFieldValue {

	private final CatalogPropertyAccesor access;
	

	@Inject 
	public WriteFieldValueImpl(CatalogPropertyAccesor access) {
		super();
		this.access = access;
	}


	@Override
	public boolean execute(Context c) throws Exception 
	{
		ContentContext context = (ContentContext) c;
		
		CatalogDescriptor catalog=context.getCatalogContext().getCatalogDescriptor();
		CatalogEntry entry=context.getOutput();
		Session session=context.assertReflectionSession(access);
		access.setPropertyValue(catalog, context.getField(), entry, context.getFieldValue(), session);
		
		return CONTINUE_PROCESSING;
	}

}
