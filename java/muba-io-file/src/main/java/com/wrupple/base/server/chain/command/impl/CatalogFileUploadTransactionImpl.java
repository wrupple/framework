package com.wrupple.base.server.chain.command.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Context;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.wrupple.muba.event.domain.CatalogKey;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.desktop.domain.WruppleImageMetadata;

public class CatalogFileUploadTransactionImpl implements CatalogFileUploadTransaction {

	private final DataStoreManager daoFactory;
	private final String fileFolder;

	@Inject
	public CatalogFileUploadTransactionImpl(DataStoreManager daoFactory, @Named("file-location") String fileFolder) {
		this.daoFactory = daoFactory;
		this.fileFolder = fileFolder;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		HttpServletRequest request = context.getRequest();
		/*
		 * FROM COMMONS-FILEUPLOAD WEB EXAPMLE
		 */

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			throw new IllegalArgumentException("Form is not multipart");
		}

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		// Parse the request
		FileItemIterator iter = upload.getItemIterator(request);
		String name;
		InputStream inputStream;
		String contentType;
		PersistentCatalogEntity image;
		String imageId;
		OutputStream outputStream;
		String threadId = String.valueOf(Thread.currentThread().getId());
		List<String> output = new ArrayList<String>(1);

		// TODO support other files not just images (GAE version too)
		CatalogDataAccessObject<PersistentCatalogEntity> dao = daoFactory.getOrAssembleDataSource(WruppleImageMetadata.CATALOG, context,
				PersistentCatalogEntity.class);
		File tempFile;
		File permanentFile;
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			if (item.isFormField()) {
			} else {
				name = item.getName();
				contentType = item.getContentType();

				tempFile = new File(fileFolder + threadId);
				inputStream = item.openStream();
				outputStream = new FileOutputStream(tempFile);

				int size_in_KB = 0;
				byte[] bytes = new byte[1024];

				try {
					while ((size_in_KB = inputStream.read(bytes)) != -1) {
						outputStream.write(bytes, 0, size_in_KB);
					}
					outputStream.close();
					inputStream.close();
				} catch (IOException e) {
					outputStream.close();
					inputStream.close();
					tempFile.delete();
					throw e;
				}

				image = new PersistentCatalogEntityImpl(dao.getCatalogType());
				image.setPropertyValue(name, CatalogKey.NAME_FIELD);
				image.setPropertyValue(contentType, WruppleImageMetadata.CONTENT);

				try {
					image = dao.create(image);
				} catch (Exception e) {
					tempFile.delete();
					throw e;
				}
				imageId = image.getIdAsString();

				try {
					permanentFile = new File(fileFolder + imageId);
					if (tempFile.renameTo(permanentFile)) {
						output.add(imageId);
					}else{
						throw new IOException("unable to rename temporal file");
					}
				} catch (Exception e) {
					tempFile.delete();
					dao.delete(image);
					throw e;
				}

			}
		}

		context.put(ImageUploadActionResponseWriter.BLOB_KEYS, output);
		return CONTINUE_PROCESSING;
	}

}
