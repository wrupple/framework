package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;

public final class JsMemoryCache extends JavaScriptObject implements CatalogCache {

	protected JsMemoryCache() {
		super();
	}

	@Override
	public native JsCatalogEntry read(String entryId) /*-{
														return this.identityCache[entryId];
														}-*/;

	@Override
	public native void put(String id, JsCatalogEntry entry) /*-{
															if(id==null){
															throw "attempted to store unidentified object in cache";
															}
															var atLeastOneProperty=false;
															for ( var k in entry) {
															atLeastOneProperty=true;
															break;
															}
															if(!atLeastOneProperty){
															throw "attempted to store an empty object in cache";
															}
															this.identityCache[id] = entry;
															//TODO skip this operation when adding entries from a result set
															var temp;
															for(var i = 0 ; i < this.naturalFilterCache.length; i++){
															temp = this.naturalFilterCache[i];
															if(id==temp.id){
															this.naturalFilterCache[i]=entry;
															}
															}
															
															}-*/;

	@Override
	public void put(int start, JsArray<JsCatalogEntry> result) {
		JsArray<JsCatalogEntry> arr = getBackingArray();
		int index;
		JsCatalogEntry entry;
		for (int i = 0; i < result.length(); i++) {
			index = start + i;
			entry = result.get(i);
			put(entry.getId(), entry);
			arr.set(index, entry);
		}
	}

	@Override
	public native void setCatalog(String catalogid) /*-{
													this.catalog = catalogid;
													this.identityCache = {};
													this.naturalFilterCache = [];
													}-*/;

	public native JsArray<JsCatalogEntry> getBackingArray()/*-{
															return this.naturalFilterCache;
															}-*/;

	@Override
	public JsArray<JsCatalogEntry> read(int start, int length) {

		JsArray<JsCatalogEntry> arr = getBackingArray();
		if (arr.length() > start) {
			JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
			JsCatalogEntry entry;
			int indexInCache;
			for (int i = 0; i < length; i++) {
				indexInCache = i + start;
				entry = arr.get(indexInCache);
				if (entry == null) {
					break;
				} else {
					regreso.push(entry);
				}

			}
			return regreso;
		} else {
			return null;
		}
	}

	@Override
	public native void remove(String id) /*-{
											this.identityCache[id]=null;
											
											var temp;
											for(var i = 0 ; i < this.naturalFilterCache.length; i++){
											temp = this.naturalFilterCache[i];
											if(id==temp.id){
											this.naturalFilterCache.length=i;
											break;
											}
											}
											
											
											}-*/;

	@Override
	public int length() {
		return getBackingArray().length();
	}

	@Override
	public native String getCatalog() /*-{
										return this.catalog;
										}-*/;

	public native JavaScriptObject getIdentityCache() /*-{
														return this.identityCache;
														}-*/;

	@Override
	public native void invalidate() /*-{
									this.identityCache = {};
									this.naturalFilterCache = [];
									this.complete=false;
									}-*/;

	@Override
	public void forceAppend(JsCatalogKey createdEntry) {
		putSkippingListCache(createdEntry.getId(), createdEntry);
		getBackingArray().push((JsCatalogEntry) createdEntry);
	}

	public native void putSkippingListCache(String id, JavaScriptObject entry) /*-{
																				if(id==null){
																				throw "attempted to store unidentified object in cache";
																				}
																				var atLeastOneProperty=false;
																				for ( var k in entry) {
																				atLeastOneProperty=true;
																				break;
																				}
																				if(!atLeastOneProperty){
																				throw "attempted to store an empty object in cache";
																				}
																				this.identityCache[id] = entry;
																				
																				}-*/;

	@Override
	public native void setComplete(boolean b) /*-{
												this.complete=b;
												}-*/;

	// TODO when all entries become available, certain statistical
	// properties become available aswell (synchronously)
	@Override
	public native boolean isComplete() /*-{
										if(this.complete==null){
										this.complete=false;
										}
										return this.complete;
										}-*/;

	@Override
	public native void setLastEntryCursor(String cursor) /*-{
		this.lastEntryCursor=cursor;
	}-*/;

	@Override
	public native String getLastEntryCursor() /*-{
		return this.lastEntryCursor;
	}-*/;

}
