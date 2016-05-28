package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.muba.catalogs.server.domain.CatalogPeer;

public interface BPMPeer extends CatalogPeer,ManagedObject {
	

	/**
	 * @return with a private key we can receive data and we know it's them for
	 *         sure
	 */
	String getPrivateKey();

	void setPrivateKey(String key);
	
	
	public void setExpirationDate(Date expirationDate);
	

	Object getLastLocation();
	
	String getBPUrlBase();
	

}
