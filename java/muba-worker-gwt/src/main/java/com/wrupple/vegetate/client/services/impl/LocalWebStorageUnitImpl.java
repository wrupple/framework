package com.wrupple.vegetate.client.services.impl;

import com.wrupple.vegetate.client.services.LocalWebStorageUnit;

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
