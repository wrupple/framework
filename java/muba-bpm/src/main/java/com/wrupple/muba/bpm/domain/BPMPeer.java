package com.wrupple.muba.bpm.domain;

import java.util.Date;

public interface BPMPeer extends ManagedObject, com.wrupple.muba.event.domain.Host {
	

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
