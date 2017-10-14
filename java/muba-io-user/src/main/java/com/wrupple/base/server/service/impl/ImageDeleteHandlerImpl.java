package com.wrupple.base.server.service.impl;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogEntry;

@Singleton
public class ImageDeleteHandlerImpl implements ImageDeleteHandler {

	private String fileFolder;

	@Inject
	public ImageDeleteHandlerImpl(@Named("file-location") String fileFolder) {
		this.fileFolder=fileFolder;
	}
	@Override
	public boolean execute(Context context) throws Exception {
		
		CatalogEntry old = (CatalogEntry) context.getOldValue();
		
		File file = new File(fileFolder+old.getIdAsString());
		if(!file.delete()){
			throw new IOException("unable to delete file "+old.getIdAsString());
		}
		
		return CONTINUE_PROCESSING;
	}

}
