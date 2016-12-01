package com.wrupple.base.server.chain.command.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.chain.command.CatalogResponseWriter;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogImageResponseWriterDictionary;
import com.wrupple.muba.desktop.domain.WruppleImageMetadata;

public class CatalogImageResponseWriterImpl implements CatalogImageResponseWriter {

	private final Provider<CatalogResponseWriter> defaultCommand;
	private final CatalogImageResponseWriterDictionary dictionary;
	private final String fileFolder;
	private final HttpServletResponse response;

	@Inject
	public CatalogImageResponseWriterImpl(Provider<CatalogResponseWriter> defaultCommand, CatalogImageResponseWriterDictionary dictionary,
			@Named("file-location") String fileFolder, HttpServletResponse response) {
		this.defaultCommand = defaultCommand;
		this.fileFolder = fileFolder;
		this.dictionary = dictionary;
		this.response = response;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		String downloadActionToken = (String) context.get(CatalogActionRequest.FORMAT_PARAMETER);
		if (downloadActionToken == null) {
			String targetAction = (String) context.get(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
			Command ccc = dictionary.getCommand(targetAction);
			if (ccc == null) {
				return defaultCommand.get().execute(context);
			} else {
				return ccc.execute(c);
			}
		} else {
			List<PersistentCatalogEntity> list = (List) context.getResults();
			PersistentCatalogEntity image = (PersistentCatalogEntity) list.get(0);
			String fileName = image.getName();
			if(fileName==null){
				fileName="file";
			}
			String mimeType = (String) image.getPropertyValue(WruppleImageMetadata.CONTENT);
			//String mimeType = getServletContext().getMimeType(file.getAbsolutePath());
			File file = new File(fileFolder + image.getIdAsString());
			if (!file.exists()) {
				throw new ServletException("File doesn't exists on server.");
			}
			InputStream fis = new FileInputStream(file);
			
			response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			ServletOutputStream os = response.getOutputStream();
			byte[] bufferData = new byte[1024];
			int read = 0;
			while ((read = fis.read(bufferData)) != -1) {
				os.write(bufferData, 0, read);
			}
			os.flush();
			os.close();
			fis.close();

		}
		return CONTINUE_PROCESSING;
	}

}
