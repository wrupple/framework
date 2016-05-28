package com.wrupple.muba.bpm.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import com.wrupple.muba.catalogs.server.service.CatalogEntryBeanDAO;
import com.wrupple.muba.catalogs.server.service.StreamingCatalogImportHandler;
import com.wrupple.vegetate.server.services.SessionContext;

public class StreamingCatalogImportHandlerImpl implements
		StreamingCatalogImportHandler {

	CatalogEntryBeanDAO genericDao;
	SessionContext sessionData;
	static final String COMMA = ",";

	@Inject
	public StreamingCatalogImportHandlerImpl(SessionContext sessionData,
			CatalogEntryBeanDAO dao) {
		super();
		this.sessionData=sessionData;
		this.genericDao = dao;
	}

	@Override
	public List<String> importCatalogData(String targetCatalog,
			Scanner scanner) throws Exception{
		List<String> regreso = new ArrayList<String>(100);
		int lineNumber = 0;
		String line;
		String[] columns = null;
		String[] values;
		// generally the created entry id
		while (scanner.hasNext()) {
			line = scanner.next();
			if (lineNumber == 0) {
				//TODO use comma as default separator on other references to split
				columns = line.split(COMMA);
			} else {
				values = line.split(COMMA);
				importValues(targetCatalog, columns, values,regreso);
			}
			lineNumber++;
		}

		return regreso;
	}

	private void importValues(String targetCatalog, String[] columns,
			String[] values, List<String> regreso) {
		// TODO Auto-generated method stubWruppleDataStoreWriter writter,
		
	}

	
}
