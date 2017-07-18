package com.wrupple.muba.cms.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface ContentManager<T extends JavaScriptObject> {
	
	
	String getCatalog();

	public com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext,JsTransactionApplicationContext> getEditingProcess(CatalogAction mode, EventBus bus,
                                                                                                                                           ProcessContextServices contextServices) ;
	
	//TODO use a property description instead of hardcoded booleans
	public com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext,JsTransactionApplicationContext> getSelectionProcess(ProcessContextServices contextServices,
                                                                                                                                             boolean multiple, boolean createAction) ;


	HumanTaskProcessor<JsArray<T>,JsFilterData> getSelectTransaction(JsTransactionApplicationContext parameter,
			JavaScriptObject properties, EventBus eventBus,
			ProcessContextServices context);
	
	HumanTaskProcessor<T,T> getCreateTransaction(JsTransactionApplicationContext parameter,
			JavaScriptObject properties, EventBus eventBus,
			ProcessContextServices context);
	
	HumanTaskProcessor<T,T> getReadTransaction(JsTransactionApplicationContext parameter,
			JavaScriptObject properties, EventBus eventBus,
			ProcessContextServices context);
	
	HumanTaskProcessor<T,T> getUpdateTransaction(JsTransactionApplicationContext parameter,
			JavaScriptObject properties, EventBus eventBus,
			ProcessContextServices context);

}
