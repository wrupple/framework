package com.wrupple.muba.desktop.client.factory.dictionary;

import com.wrupple.muba.desktop.client.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.services.presentation.BrowserSelectionModel;

public interface SelectionModelDictionary extends ServiceDictionary<BrowserSelectionModel> {
	/*
	 * Properties
	 */
	String SELECTION_HANDLER = "selectionHandler";
	/*
	 * Values
	 */
	String NO_SELECTION_HANDLER = "void";

}
