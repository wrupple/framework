package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.UploadURlResponsetHandler;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.services.ObjectMapper;

public class UploadURlResponsetHandlerImpl implements UploadURlResponsetHandler {

	final ObjectMapper mapper;
	
	@Inject 
	public UploadURlResponsetHandlerImpl(ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}


	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		HttpServletResponse resp = context.getRequest().getServletContext().getResponse();
		List<String> uploadedBlobKeys = (List<String>) context.get(UPLOAD_URLS);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		System.err.print("deploy upload service url " + uploadedBlobKeys);
		mapper.writeValue(out, uploadedBlobKeys);
		return CONTINUE_PROCESSING;
	}

}
