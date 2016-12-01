package com.wrupple.muba.catalogs.server.service;

import java.util.List;
import java.util.Scanner;

public interface StreamingCatalogImportHandler {

	/**
	 * 
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
	 * @param targetCatalog
	 * @param scanner
	 * @return  a list of the created entries ids
	 * @throws DataLayerException 
	 */
	List<String> importCatalogData(String targetCatalog, Scanner scanner) throws Exception;

}
