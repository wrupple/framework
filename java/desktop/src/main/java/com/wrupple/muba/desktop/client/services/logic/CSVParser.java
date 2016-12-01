package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;

public interface CSVParser {
	
	ImportData parse(String csv);
}
