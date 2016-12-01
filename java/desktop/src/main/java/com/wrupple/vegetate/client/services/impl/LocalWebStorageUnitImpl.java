package com.wrupple.vegetate.client.services.impl;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.domain.Transaction;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.client.services.LocalWebStorageUnit;
import com.wrupple.vegetate.domain.CatalogDescriptor;

/**
 * Store id-name pairs 
 * 
 * http://www.w3schools.com/html/html5_webstorage.asp
 * 
 * @author japi
 *
 */
public class LocalWebStorageUnitImpl implements LocalWebStorageUnit {

	public native boolean isSupported()/*-{
		try {
			return 'localStorage' in window && window['localStorage'] !== null;
		} catch (e) {
			return false;
		}
	}-*/;

	@Override
	public Transaction startTransaction() {
		// TODO Auto-generated method stub
		return null;
	}


}
