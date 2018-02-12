package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.shared.services.factory.ServiceDictionary;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;
import com.wrupple.muba.desktop.shared.services.event.EntriesDeletedEvent;
import com.wrupple.muba.desktop.shared.services.event.EntriesRetrivedEvent;
import com.wrupple.muba.desktop.shared.services.event.EntryCreatedEvent;
import com.wrupple.muba.desktop.shared.services.event.EntryUpdatedEvent;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.vegetate.domain.CatalogDescriptor;

import java.util.List;

public abstract class UserInteractionWidgetImpl<T extends JavaScriptObject,R> extends ResizeComposite implements
		HumanTaskProcessor<T,R>, TakesValue<T>  {

	private String catalogid;
	private CatalogDescriptor catalog;
	
	protected JavaScriptObject properties;
	protected final ServiceDictionary configurationService;
	
	
	public UserInteractionWidgetImpl(ServiceDictionary configurationService) {
		super();
		this.configurationService = configurationService;
	}



	@Override
	public void onEntriesRetrived(EntriesRetrivedEvent e) {
		// Is there an update on what we think is in the server?
		String eventCatalog = e.getCatalog();
		List<JsCatalogEntry> eventEntries = e.getEntries();
		if (catalogid != null && getCatalog() != null && catalogid.equals(eventCatalog)) {
			JavaScriptObject currentValue = getValue();
			if (currentValue != null) {
				String currentEntryId = GWTUtils.getAttribute(currentValue,JsCatalogEntry.ID_FIELD);
				if (currentEntryId != null) {
					String tempEntryId;
					for (JsCatalogKey entry : eventEntries) {
						tempEntryId = entry.getId();
						if (tempEntryId != null) {
							if (tempEntryId.equals(currentEntryId)) {
								setValue((T) entry);
							}
						}
					}
				}
			}
		}
	}
	
	

	@Override
	public void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent) {
		// let underlying pipes update cache and fire retriving events and catch
		// those!
	}

	@Override
	public void onEntryCreated(EntryCreatedEvent entryCreatedEvent) {
		// not much to do is there?

	}
	
	@Override
	public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
		// TODO Auto-generated method stub
		
	}

	
	public CatalogDescriptor getCatalog() {
		return catalog;
	}
	
	public String getCatalogId() {
		return catalogid;
	}

	public void setCatalogid(String catalogid) {
		this.catalogid = catalogid;
	}

	public void setCatalog(CatalogDescriptor catalog) {
		this.catalog = catalog;
	}

	@Override
    public void applyAlterations(ReconfigurationBroadcastEvent properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParameters) {
        this.properties=properties;//rewrite options, rather
		onBeforeRecofigure(properties, contextServices, eventBus, contextParameters);
		
		configurationService.reconfigure(properties,this, contextServices, eventBus, contextParameters);
		
		onAfterReconfigure(properties,contextServices,eventBus,contextParameters);
	}


    protected abstract void onAfterReconfigure(ReconfigurationBroadcastEvent properties2,
                                               ProcessContextServices contextServices, EventBus eventBus,
                                               JsTransactionApplicationContext contextParameters);


    protected abstract void onBeforeRecofigure(ReconfigurationBroadcastEvent properties2,
                                               ProcessContextServices contextServices, EventBus eventBus,
                                               JsTransactionApplicationContext contextParameters) ;
}
