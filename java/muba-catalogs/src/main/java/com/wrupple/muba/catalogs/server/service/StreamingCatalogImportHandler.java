package com.wrupple.muba.catalogs.server.service;

import java.util.List;
import java.util.Scanner;

public interface StreamingCatalogImportHandler {

	/**
	 * @param targetCatalog
	 * @param scanner
	 * @return  a list of the created entries ids
	 * @throws DataLayerException 
	 */
	List<String> importCatalogData(String targetCatalog, Scanner scanner) throws Exception;

}
