package com.wrupple.muba.desktop.shared.services;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public interface CSVWriter {

	String parseEntryFieldsToCSV(Collection<String> fieldset, List<? extends JavaScriptObject> list);

}