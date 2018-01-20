package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.client.services.CreditCardStorageUnit;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

import java.util.List;

public class NoOpCreditCardStorageUnit implements CreditCardStorageUnit {


	@Override
	public Transaction startTransaction() {
		return null;
	}

	@Override
	public void create(String domainNamespace, CatalogEntry entry, CatalogDescriptor catalog, StateTransition<CatalogEntry> callback) {
		callback.setResultAndFinish(entry);
	}

	@Override
	public <T extends JavaScriptObject> void read(String domainNamespace, String id, CatalogDescriptor catalog, StateTransition<T> callback) {
		callback.setResultAndFinish(null);		
	}

	@Override
	public <T extends JavaScriptObject> void read(String domainNamespace, JsFilterData filter, CatalogDescriptor catalog, StateTransition<List<T>> callback) {
		callback.setResultAndFinish(null);		
	}

	@Override
	public void read(String domainNamespace, List<String> ids, CatalogDescriptor catalog, StateTransition<List<CatalogEntry>> callback) {
		callback.setResultAndFinish(null);		
	}

	@Override
	public void update(String domainNamespace, String id, CatalogEntry entry, CatalogDescriptor catalog, StateTransition<CatalogEntry> callback) {
		callback.setResultAndFinish(entry);
	}

	@Override
	public void delete(String domainNamespace, String id, CatalogDescriptor catalog, StateTransition<CatalogEntry> callback) {
		callback.setResultAndFinish(null);
	}

}
