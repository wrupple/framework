package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.factory.dictionary.DictionaryRegistry;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;
@Singleton
public class ServicePickerCellProvider implements CatalogFormFieldProvider {

	DictionaryRegistry registry;
	
	@Inject
	public ServicePickerCellProvider(DictionaryRegistry registry) {
		super();
		this.registry = registry;
	}



	@Override
	public Cell<? extends Object> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		
		if(CatalogAction.READ==mode){
			return new TextCell();
		}else{
			String widgetId = d.getWidget();
			ServiceDictionary<?> service = registry.get(widgetId);
			assert service!=null : "No such service "+widgetId;
			List<String> options = new ArrayList<String>();
			options.add("...");
			options.addAll(service.keySet());
			Cell<String> wrapped = new SelectionCell(options);
			return wrapped;
		}
		
	}

}
