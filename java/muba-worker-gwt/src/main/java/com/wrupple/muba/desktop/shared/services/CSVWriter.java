package com.wrupple.muba.desktop.shared.services;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Collection;
import java.util.List;

public interface CSVWriter {

	String parseEntryFieldsToCSV(Collection<String> fieldset, List<? extends JavaScriptObject> list);

}