package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;

import com.wrupple.muba.event.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.server.chain.command.UploadURlResponsetHandler;

public class UploadURlResponsetHandlerImpl extends GenericResponseWriterImpl implements UploadURlResponsetHandler {

	
	@Inject 
	public UploadURlResponsetHandlerImpl(ObjectMapper mapper) {
		super(mapper,UPLOAD_URLS);
	}

}
