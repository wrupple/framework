package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.Host;

public interface CatalogPeer extends Host {
	/**
	 * @return the platform the peer is running on
	 */
	String getAgent();
	void setAgent(String s);
	
	/**
	 * this is usually just the entry's domain as a String
	 * 
	 * @return the domain keys have access to in the foreign server
	 */
	String getCatalogDomain();
	
	/**
	 * used to support multiple users logged in a peer, this value represents the state in the catalog provider side that spawned this peer
	 * 
	 * @return
	 */
	int getStakeHolderIndex();

	String getCatalogUrlBase();
	
}
