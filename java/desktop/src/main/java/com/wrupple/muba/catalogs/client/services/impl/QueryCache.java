package com.wrupple.muba.catalogs.client.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public class QueryCache implements CatalogCache {
	
	private final CatalogCache identityCache;
	private boolean complete;
	private List<String> list;
	private String catalog;
	private String lastEntryCursor;
	
	public QueryCache(CatalogCache identityCache) {
		super();
		this.identityCache = identityCache;
		list = new ArrayList<String>();
	}
	
	@Override
	public JsArray<JsCatalogEntry> read(int start, int length) {
		if(list.size()>start){
			JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
			JsCatalogEntry entry;
			int indexInCache ;
			String id;
			for(int i = 0 ; i < length ; i++){
				indexInCache= i+start;
				if(indexInCache< list.size()){
					id = list.get(indexInCache);
					entry = identityCache.read(id);
					if(entry==null){
						break;
					}else{
						regreso.push(entry);
					}
				}else{
					break;
				}
			}
			return regreso;
		}else{
			return null;
		}
	}

	@Override
	public void put(int start, JsArray<JsCatalogEntry> result) {
		int index;
		JsCatalogEntry entry;
		for(int i = 0 ; i < result.length(); i++){
			index=start+i;
			entry = result.get(i);
			put(entry.getId(), entry);
			if(index<list.size()){
				list.set(index, entry.getId());
			}else{
				list.add( entry.getId());
			}
		}
	}
	
	@Override
	public int length() {
		return list.size();
	}
	
	@Override
	public void invalidate() {
		list.clear();
	}
	
	@Override
	public void setComplete(boolean b) {
		this.complete=b;
	}

	@Override
	public boolean isComplete() {
		return complete;
	}
	

	@Override
	public JsCatalogEntry read(String entryId) {
		return identityCache.read(entryId);
	}

	@Override
	public void put(String id, JsCatalogEntry entry) {
		identityCache.put(id, entry);
	}


	@Override
	public void remove(String id) {
		identityCache.remove(id);
	}

	

	

	@Override
	public void forceAppend(JsCatalogKey createdEntry) {
		// TODO Auto-generated method stub
		identityCache.forceAppend(createdEntry);
	}

	@Override
	public void setCatalog(String catalogid) {
		this.catalog=catalogid;
	}

	@Override
	public String getCatalog() {
		return catalog;
	}

	@Override
	public void setLastEntryCursor(String cursor) {
		this.lastEntryCursor=cursor;
	}

	@Override
	public String getLastEntryCursor() {
		return lastEntryCursor;
	}


}
