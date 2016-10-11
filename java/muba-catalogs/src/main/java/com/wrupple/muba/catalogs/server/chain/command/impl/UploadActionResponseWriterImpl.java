package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;

import com.wrupple.muba.bootstrap.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.server.chain.command.UploadActionResponseWriter;

public class UploadActionResponseWriterImpl extends GenericResponseWriterImpl implements UploadActionResponseWriter {
	@Inject
	public UploadActionResponseWriterImpl(ObjectMapper mapper) {
		super(mapper, BLOB_KEYS);
	}

}
