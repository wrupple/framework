package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

/**
 * @author japi
 * 
 */
public interface CatalogFormFieldProvider {

	/**EventBus bus,ProcessManager pm,
	 * @param bus 
	 * @param pm 
	 * @param d
	 * @param mode 
	 * @return a cell capable of editing a value directly extracted from the corresponding field in the JavaScriptObject
	 * @throws Exception 
	 */
	Cell<? extends Object> createCell(EventBus bus, ProcessContextServices contextServices,JsTransactionActivityContext contextParameters, JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) ;

}