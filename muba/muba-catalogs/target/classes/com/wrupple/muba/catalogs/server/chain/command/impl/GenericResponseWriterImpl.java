package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.io.PrintWriter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.vegetate.server.services.ObjectMapper;

public class GenericResponseWriterImpl implements Command {
	ObjectMapper om ;
	private String contextParameter;
	private Provider<HttpServletResponse> respp;
	
	public GenericResponseWriterImpl(ObjectMapper om,String contextParameter,Provider<HttpServletResponse>  resp) {
		super();
		this.om = om;
		this.contextParameter=contextParameter;
		this.respp=resp;
		
	}


	@Override
	public boolean execute(Context context) throws Exception {
		Object parameter = context.get(contextParameter);
		HttpServletResponse resp = respp.get();
		resp.setContentType(om.getMimeType());
		resp.setCharacterEncoding(om.getCharacterEncoding());
		PrintWriter out = resp.getWriter();
		om.writeValue(out, parameter);
		
		return CONTINUE_PROCESSING;
	}

	
	
}
