package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;


public interface CatalogEditor<V extends JavaScriptObject> extends HumanTaskProcessor<V,V>,LeafValueEditor<V>, IsWidget {
	 void reset();
	
	void initialize(String catalog,CatalogAction mode,EventBus bus,ProcessContextServices contextServices,JavaScriptObject properties,JsTransactionActivityContext contextParameters);
	
	CatalogAction getMode();
	
	String getCatalogId();
	
	 void setFieldValue(String fieldId, Object value) ;

	 Object getFieldValue(String field);
	
}
