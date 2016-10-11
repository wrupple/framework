package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.io.PrintWriter;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;

public class GenericResponseWriterImpl implements Command {
	ObjectMapper om;
	private String contextParameter;

	public GenericResponseWriterImpl(ObjectMapper om, String contextParameter) {
		super();
		this.om = om;
		this.contextParameter = contextParameter;

	}

	@Override
	public boolean execute(Context context) throws Exception {
		Object parameter = context.get(contextParameter);
		PrintWriter out = ((CatalogActionContext) context).getExcecutionContext().getScopedWriter(context);
		om.writeValue(out, parameter);
		return CONTINUE_PROCESSING;
	}

}
