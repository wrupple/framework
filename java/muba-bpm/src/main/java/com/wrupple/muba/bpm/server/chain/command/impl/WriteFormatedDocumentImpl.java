package com.wrupple.muba.bpm.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bpm.domain.Document;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;

@Singleton
public class WriteFormatedDocumentImpl implements WriteFormatedDocument {
	protected static final Logger log = LoggerFactory.getLogger(WriteFormatedDocumentImpl.class);
	private final CatalogEvaluationDelegate accessor;
	
	@Inject
	public WriteFormatedDocumentImpl(CatalogEvaluationDelegate accessor) {
		super();
		this.accessor=accessor;
	}
	
	
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext ccontext = (CatalogActionContext) c;
		List<Document> list = ccontext.getResults();
		if (list != null && !list.isEmpty()) {
			PrintWriter out = ccontext.getExcecutionContext().getScopedWriter(ccontext);
			String value;
			for (Document sheet : list) {
				value = sheet.getValue();
				accessor.evalTemplate(value, out,  ccontext.getLocale(), ccontext);
			}
			out.close();
		} else {
			throw new IllegalArgumentException("no html sources collected with the given Id");
		}
		return CONTINUE_PROCESSING;
	}

}
