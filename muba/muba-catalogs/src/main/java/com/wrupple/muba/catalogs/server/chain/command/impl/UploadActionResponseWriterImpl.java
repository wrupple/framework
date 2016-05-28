package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.UploadActionResponseWriter;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.services.ObjectMapper;

public class UploadActionResponseWriterImpl implements UploadActionResponseWriter {

	final ObjectMapper mapper;
	
	@Inject
	public UploadActionResponseWriterImpl(ObjectMapper mapper) {
		this.mapper=mapper;
	}
	
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		HttpServletResponse resp = context.getRequest().getServletContext().getResponse();
		List<String> uploadedBlobKeys = (List<String>) context.get(BLOB_KEYS);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		System.err.print("new blobkeys " + uploadedBlobKeys);
		mapper.writeValue(out, uploadedBlobKeys);
		return CONTINUE_PROCESSING;
	}

}
