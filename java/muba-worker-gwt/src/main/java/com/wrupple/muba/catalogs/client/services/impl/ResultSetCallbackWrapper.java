package com.wrupple.muba.catalogs.client.services.impl;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.vegetate.client.services.CatalogEntryAssembler;

import java.util.Collections;
import java.util.List;

public class ResultSetCallbackWrapper extends
		DataCallback<JsCatalogActionResult> {
	
	private final StateTransition<List<JsCatalogEntry>> wrapped;
	private final CatalogEntryAssembler entryAssembler;
	private final ClientCatalogCacheManager ccm;
	private final JsFilterData filter;

	public ResultSetCallbackWrapper( ClientCatalogCacheManager ccm,CatalogEntryAssembler summaryService,StateTransition<List<JsCatalogEntry>> wrapped) {
		this(ccm,summaryService,wrapped,null);
		
	}
	
	public ResultSetCallbackWrapper(ClientCatalogCacheManager ccm,CatalogEntryAssembler summaryService,StateTransition<List<JsCatalogEntry>> wrapped, JsFilterData filter) {
		this.ccm=ccm;
		this.wrapped = wrapped;
		this.entryAssembler=summaryService;
		this.filter=filter;
		
	}

	@Override
	public void execute() {
		if (result.getResponse() == null) {
			wrapped.setResultAndFinish(null);
		} else {
			
			JsArray<JsVegetateColumnResultSet> returnList =   result.getResponseAsJSOList().cast();
			if(returnList!=null && returnList.length() > 0 ){
				List<JsCatalogEntry> list = processResultSets(returnList);
				wrapped.setResultAndFinish(list);
			}else{
				GWT.log("Received empty response");
				wrapped.setResultAndFinish(Collections.EMPTY_LIST);
			}
		}
		
	}
	
	
	private List<JsCatalogEntry> processResultSets(JsArray<JsVegetateColumnResultSet> resultSets) {
		JsArray<JsCatalogEntry> assembledEntries = null;
		JsArray<JsCatalogEntry> assembledForeignEntries;
		String foreignCatalog;
		JsVegetateColumnResultSet resultSet;
		CatalogCache foreignCatalogCache;
		int ammountOfResults = resultSets.length();
		for (int i =  0; i < ammountOfResults; i++) {
			resultSet = resultSets.get(i);
			foreignCatalog = resultSet.getId();
			assembledForeignEntries = entryAssembler.processResultSet(resultSet, foreignCatalog);
			if(assembledEntries==null){
				assembledEntries= assembledForeignEntries;
				filter.setCursor(resultSet.getCursor());
			}
			foreignCatalogCache = ccm.getIdentityCache(foreignCatalog);
			if(foreignCatalogCache!=null&&assembledForeignEntries!=null){
				putEntriesInIdentityCache(foreignCatalogCache,assembledForeignEntries,foreignCatalog);
			}
		}
		List<JsCatalogEntry> list = JsArrayList.arrayAsList(assembledEntries);
		return list;
	}


	private void putEntriesInIdentityCache(CatalogCache cache,
			JsArray<JsCatalogEntry> entries, String catalog) {
		JsCatalogEntry entry;
		for(int i = 0; i < entries.length(); i++){
			entry = entries.get(i);
			cache.put(entry.getId(), entry);
		}
	}

}
