package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.server.domain.ContentContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;

public class WriteFieldValueImpl implements Command {

	private final CatalogEvaluationDelegate access;
	

	@Inject 
	public WriteFieldValueImpl(CatalogEvaluationDelegate access) {
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
